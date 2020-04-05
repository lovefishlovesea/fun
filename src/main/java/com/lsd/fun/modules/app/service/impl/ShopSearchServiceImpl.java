package com.lsd.fun.modules.app.service.impl;

import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.ShopSortUtil;
import com.lsd.fun.modules.app.dto.LocationDto;
import com.lsd.fun.modules.app.dto.SearchResultDto;
import com.lsd.fun.modules.app.query.MapSearchQuery;
import com.lsd.fun.modules.app.vo.ShopBucketByArea;
import com.lsd.fun.modules.app.dto.ShopIndexKey;
import com.lsd.fun.modules.app.service.ShopSearchService;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.dto.ShopVO;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import com.lsd.fun.modules.cms.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lsd
 * 2020-04-03 10:23
 */
@Slf4j
@Service
public class ShopSearchServiceImpl implements ShopSearchService {

    @Autowired
    private RestHighLevelClient rhlClient;
    @Autowired
    private BaiduLBSService baiduLBSService;
    @Autowired
    private ShopService shopService;

    @Override
    public List<ShopBucketByArea> aggBySubArea(String cityName) {
        // 先根据城市filter再聚合
        BoolQueryBuilder qb = QueryBuilders.boolQuery().filter(
                QueryBuilders.termQuery(ShopIndexKey.CITY, cityName)
        );
        SearchSourceBuilder sb = new SearchSourceBuilder()
                .query(qb)
                .aggregation(
                        AggregationBuilders.terms(ShopIndexKey.AGG_REGION)     //聚合名称
                                .field(ShopIndexKey.REGION)            //根据区级行政区聚合
                );
        SearchRequest searchReq = new SearchRequest(ShopIndexKey.INDEX_NAME).source(sb);
        log.debug(searchReq.toString());
        try {
            final SearchResponse response = rhlClient.search(searchReq, RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK) {
                log.error("地图页面城市信息聚合失败,searchRequest = {}", searchReq.toString());
                throw new RRException("获取地区聚合信息失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            // 聚合信息转为DTO返回
            Terms terms = response.getAggregations().get(ShopIndexKey.AGG_REGION);
            return terms.getBuckets().stream().map(buck -> {
                // 请求百度地图获取地区经纬度
                String region = buck.getKeyAsString();
                BaiduMapLocation location = baiduLBSService.parseAddress2Location(region);
                return new ShopBucketByArea(region, buck.getDocCount(), location.getLongitude(), location.getLatitude());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取地区聚合信息失败", e);
            throw new RRException("获取地区聚合信息失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PageUtils mapSearchByCity(MapSearchQuery query) {
        // ES搜索出此城市的所有房源id
        QueryBuilder boolQB = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(ShopIndexKey.CITY, query.getCity()));
        final SearchResultDto searchResult = this.searchES(query, boolQB);
        return this.searchDB(query.getPage(), query.getLimit(), searchResult);
    }

    @Override
    public PageUtils mapSearchByBound(MapSearchQuery query) {
        // ES搜索出当前地图视野边界范围的所有房源id
        GeoBoundingBoxQueryBuilder boundingBoxQB = QueryBuilders.geoBoundingBoxQuery(ShopIndexKey.LOCATION)
                .setCorners(
                        new GeoPoint(query.getLeftLatitude(), query.getLeftLongitude()),
                        new GeoPoint(query.getRightLatitude(), query.getRightLongitude())
                );
        BoolQueryBuilder boolQB = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ShopIndexKey.CITY, query.getCity()))
                .filter(boundingBoxQB);
        final SearchResultDto searchResult = this.searchES(query, boolQB);
        return this.searchDB(query.getPage(), query.getLimit(), searchResult);
    }


    /**
     * 查询数据库
     *
     * @param searchResult ES搜索结果
     */
    private PageUtils searchDB(Integer start, Integer size, SearchResultDto searchResult) {
        final Map<Integer, LocationDto> id2LocationMap = searchResult.getResultMap();
        final long total = searchResult.getTotal();
        if (total == 0 || MapUtils.isEmpty(id2LocationMap)) {
            return new PageUtils(Collections.emptyList(), start, size, 0, false);
        }
        // 查询数据库
        List<ShopVO> shopVOS = shopService.queryList(id2LocationMap.keySet());
        for (ShopVO vo : shopVOS) {
            LocationDto dto = id2LocationMap.get(vo.getId());
            vo.setDistance(dto.getDistance());
            vo.setLat(dto.getLat());
            vo.setLng(dto.getLng());
        }
        return new PageUtils(shopVOS, start, size, (int) total, false);
    }


    /**
     * 搜索ES
     *
     * @param qb 搜索条件构造器
     * @return Map<ID, 与用户的距离>
     */
    private SearchResultDto searchES(MapSearchQuery query, QueryBuilder qb) {
        Map<Integer, LocationDto> id2LocationMap = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("lon", query.getLng());
        params.put("lat", query.getLat());
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(qb)
                .scriptField("distance",
                        new Script(ScriptType.INLINE, "expression", "haversin(lat,lon,doc['location'].lat,doc['location'].lon)", params)
                )
                .fetchSource(new String[]{ShopIndexKey.ID, ShopIndexKey.LOCATION}, null)  //只查出houseId，避免其他无用数据浪费性能
                .sort(ShopSortUtil.getSortKey(query.getOrder_field()), SortOrder.fromString(query.getOrder()))
                .from(query.getPage())
                .size(query.getLimit());
        final SearchRequest searchRequest = new SearchRequest(ShopIndexKey.INDEX_NAME).source(sourceBuilder);
        log.debug(searchRequest.toString());
        try {
            final SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK) {
                log.error("房源搜索失败，searchRequest = {}", searchRequest.toString());
                return new SearchResultDto(0, id2LocationMap);
            }
            for (SearchHit hit : response.getHits().getHits()) {
                int id = new Integer(hit.getSourceAsMap().get(ShopIndexKey.ID).toString());
                String location = hit.getSourceAsMap().get(ShopIndexKey.LOCATION).toString();
                final String[] lat_lng = location.split(",");
                BigDecimal distanceKm = new BigDecimal(hit.getFields().get(ShopIndexKey.DISTANCE).getValue().toString());
                // km -> m 然后取整
                int distanceM = distanceKm.multiply(BigDecimal.valueOf(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue();

                id2LocationMap.put(id, new LocationDto(distanceM, new Double(lat_lng[0]), new Double(lat_lng[1])));
            }
            return new SearchResultDto(response.getHits().getTotalHits().value, id2LocationMap);
        } catch (IOException e) {
            log.error("房源搜索失败", e);
            throw new RRException("获取房源信息失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }


}
