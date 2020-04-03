package com.lsd.fun.modules.app.service;

import com.lsd.fun.modules.app.dto.ShopBucketByArea;
import com.lsd.fun.modules.cms.query.AreaQuery;

import java.util.List;

/**
 * Created by lsd
 * 2020-04-03 10:22
 */
public interface ShopSearchService {
    /**
     * 根据选定城市聚合子地区商铺信息
     */
    List<ShopBucketByArea> aggBySubArea(AreaQuery query);
}
