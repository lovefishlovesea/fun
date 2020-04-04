package com.lsd.fun.modules.app.service;

import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.app.query.MapSearchQuery;
import com.lsd.fun.modules.app.vo.ShopBucketByArea;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lsd
 * 2020-04-03 10:22
 */
public interface ShopSearchService {
    /**
     * 根据选定城市聚合子地区商铺信息
     */
    List<ShopBucketByArea> aggBySubArea(String cityName);

    /**
     * 地图查询整个城市的房源
     */
    PageUtils mapSearchByCity(MapSearchQuery query);

    /**
     * 地图查询当前地图视野边界范围内的房源
     */
    PageUtils mapSearchByBound(MapSearchQuery query);
}
