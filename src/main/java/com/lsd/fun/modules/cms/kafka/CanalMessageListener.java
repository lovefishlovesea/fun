package com.lsd.fun.modules.cms.kafka;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.modules.cms.dao.ShopDao;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 由 Canal 监听 MySQL 并发消息到 Kafka，由此监听器消费消息并更新到 ES 或 Hive
 * <p>
 * Created by lsd
 * 2020-01-31 10:49
 */
@Slf4j
@Component
public class CanalMessageListener {

    public final static String CANAL_TOPIC = "example";
    private final static String INDEX_NAME = "shop";

    @Autowired
    private Gson gson;
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private BaiduLBSService baiduLBSService;
    @Autowired
    private RestHighLevelClient rhlClient;

    @KafkaListener(topics = CANAL_TOPIC)
    public void handlerMessage(String content) {
        // parse message
        JsonObject msgObject = gson.fromJson(content, JsonObject.class);
        // 若不是我们目标数据库的变更则返回
        if (!StringUtils.equals(msgObject.get("database").getAsString(), "fun")) {
            return;
        }
        log.debug("收到消息：{}", msgObject);
        // 获取所有更新行
        JsonArray rowChangeList = msgObject.getAsJsonArray("data");
        // 同步到ES中
        for (JsonElement rowData : rowChangeList) {
            if (rowData == null) {
                return;
            }
            index2ES(msgObject, rowData);
        }
    }


    private void index2ES(JsonObject msgObject, JsonElement rowData) {
        // 根据去Binlog对应表中查询待同步到ES的数据
        String table = msgObject.get("table").getAsString();
        final int idOfTable = rowData.getAsJsonObject().get("id").getAsInt();
        QueryWrapper<Object> wrapper = Wrappers.query();
        switch (table) {
            case "seller":
                wrapper.eq("seller.id", idOfTable);
                break;
            case "category":
                wrapper.eq("category.id", idOfTable);
                break;
            case "shop":
                wrapper.eq("shop.id", idOfTable);
                break;
            default:
                return;
        }
        List<Map<String, Object>> needIndexDataList = shopDao.queryNeedIndexRow(wrapper);
        for (Map<String, Object> row : needIndexDataList) {
            // 调用百度LBS把地址"address"字段替换为经纬度"location"
            BaiduMapLocation location = baiduLBSService.parseAddress2Location(row.get("address").toString());
            row.put("location", location.getLatitude() + "," + location.getLongitude());
            row.remove("address");
        }
        // index to ES
        for (Map<String, Object> map : needIndexDataList) {
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .id(String.valueOf(map.get("id")))
                    .source(map);
            try {
                rhlClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error("Binlog索引同步失败", e);
                throw new RRException("Binlog索引同步失败");
            }
        }
    }


}
