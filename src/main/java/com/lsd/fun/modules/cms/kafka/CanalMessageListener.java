package com.lsd.fun.modules.cms.kafka;

import com.alibaba.otter.canal.protocol.Message;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lsd.fun.modules.cms.dao.ShopDao;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.alibaba.otter.canal.protocol.CanalEntry.*;

/**
 * 由 Canal 监听 MySQL 并发消息到 Kafka，由此监听器消费消息并更新到 ES 或 Hive
 * canal.mq.flatMessage = false的情况，canal-server拉取到多少Binlog就把多少变更行写到一个Kafka消息中，效率较高
 * <p>
 * Created by lsd
 * 2020-04-03 18:22
 */
@Slf4j
@Component
public class CanalMessageListener {

    public final static String CANAL_TOPIC = "example";
    private final static String INDEX_NAME = "shop";

    @Autowired
    private ShopDao shopDao;
    @Autowired
    private BaiduLBSService baiduLBSService;
    @Autowired
    private RestHighLevelClient rhlClient;

    /**
     * canal.mq.flatMessage = false的情况，手动解析Canal二进制形式（protobuf格式）的Message
     */
    @KafkaListener(topics = CANAL_TOPIC, properties = {
            "key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer=com.alibaba.otter.canal.client.kafka.MessageDeserializer"
    })
    public void handlerMessage(Message content) throws Exception {
        List<Entry> entries = content.getEntries();
        // 收集所有变更数据行的表名和id
        ArrayListMultimap<String, Integer> changedRecordIdsMap = ArrayListMultimap.create();
        // 若有数据Row更新，则解析处理Binlog，获取所在表和id
        for (Entry entry : entries) {
            changedRecordIdsMap.putAll(this.publishCanalEvent(entry));
        }
        // 去对应的table批量查询所有待更新数据
        List<Map<String, Object>> needIndexDataList = new ArrayList<>();
        for (String table : changedRecordIdsMap.keySet()) {
            List<Integer> ids = changedRecordIdsMap.get(table);
            needIndexDataList.addAll(this.processOneBinlogRow(table, ids));
        }
        // 批量同步到ES
        this.bulkIndex2ES(needIndexDataList);
    }

    /**
     * 解析处理Binlog
     *
     * @return @formatter:off   Map<table,List<idOfTable>>   @formatter:on
     */
    private Multimap<String, Integer> publishCanalEvent(Entry entry) throws IOException {
        Header header = entry.getHeader();
//        EventType eventType = header.getEventType();
//        String database = header.getSchemaName();
        String table = header.getTableName();
        RowChange rowChange;
        ArrayListMultimap<String, Integer> table2IdsMap = ArrayListMultimap.create();
        try {
            rowChange = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            log.error("Binlog索引同步失败，原因：publishCanalEvent failed", e);
            throw e;
        }
        for (RowData rowData : rowChange.getRowDatasList()) {
            List<Column> columns = rowData.getAfterColumnsList();
            // 获取主键列
            for (Column col : columns) {
                if (col.getIsKey() && StringUtils.equals(col.getName(), "id")) {
                    table2IdsMap.put(table, new Integer(col.getValue()));
                    break;
                }
            }
            // 列信息转换为Map结构
//          Map<String, Integer> columnDataMap = columns.stream()
//                  .filter(Objects::nonNull)
//                  .collect(Collectors.toMap(Column::getName, Column::getValue, (oldKey, newKey) -> newKey));
//          return columnDataMap;
        }
        return table2IdsMap;
    }

    /**
     * 根据Binlog的变更数据行信息，去对应表中查询待同步到ES的数据
     *
     * @param table      变更记录行所在数据表
     * @param idsOfTable 数据表中变更记录行的id列表
     */
    private List<Map<String, Object>> processOneBinlogRow(String table, List<Integer> idsOfTable) {
        if (CollectionUtils.isEmpty(idsOfTable)) {
            return Collections.emptyList();
        }
        QueryWrapper<Object> wrapper = Wrappers.query();
        switch (table) {
            case "seller":
                wrapper.in("seller.id", idsOfTable);
                break;
            case "category":
                wrapper.in("category.id", idsOfTable);
                break;
            case "shop":
                wrapper.in("shop.id", idsOfTable);
                break;
            default:
                return Collections.emptyList();
        }
        // 一条数据Row是一个Map，返回这种结构方便 ES API 直接使用
        List<Map<String, Object>> needIndexDataList = shopDao.queryNeedIndexRow(wrapper);
        // 调用百度LBS把地址"address"字段替换为经纬度"location"
        for (Map<String, Object> row : needIndexDataList) {
            BaiduMapLocation location = baiduLBSService.parseAddress2Location(row.get("address").toString());
            row.put("location", location.getLatitude() + "," + location.getLongitude());
            row.remove("address");
        }
        return needIndexDataList;
    }

    /**
     * Sync to ES
     * 优化效果：不使用bulk的话306条记录同步需要>1分钟，优化后只需要
     *
     * @param needIndexDataList 当前Kafka所有Binlog消息解析后得到的待更新数据
     */
    private void bulkIndex2ES(List<Map<String, Object>> needIndexDataList) throws IOException {
        // 应使用 Bulk Api 批量更新提高效率
        BulkRequest bulkReq = new BulkRequest()
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);  //写入完成立即刷新;
        for (Map<String, Object> map : needIndexDataList) {
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .id(String.valueOf(map.get("id")))
                    .source(map);
            bulkReq.add(indexRequest);
        }
        try {
            BulkResponse responses = rhlClient.bulk(bulkReq, RequestOptions.DEFAULT);
            int total = responses.getItems().length;
            int failed = 0;
            for (BulkItemResponse response : responses) {
                if (response.isFailed()) {
                    failed++;
                }
            }
            log.info("Binlog索引同步完成，共{}条记录，成功{}条，失败{}条", total, total - failed, failed);
        } catch (Exception e) {
            log.error("Binlog索引同步失败，原因：bulkIndex2ES failed", e);
            throw e;
        }
    }

}
