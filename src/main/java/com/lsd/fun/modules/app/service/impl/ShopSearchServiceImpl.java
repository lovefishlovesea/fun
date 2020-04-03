package com.lsd.fun.modules.app.service.impl;

import com.lsd.fun.modules.app.dto.ShopBucketByArea;
import com.lsd.fun.modules.app.service.ShopSearchService;
import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.lsd.fun.modules.cms.query.AreaQuery;
import com.lsd.fun.modules.cms.service.AreaService;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lsd
 * 2020-04-03 10:23
 */
@Service
public class ShopSearchServiceImpl implements ShopSearchService {

    @Autowired
    private AreaService areaService;
    @Autowired
    private RestHighLevelClient rhlClient;

    @Override
    public List<ShopBucketByArea> aggBySubArea(AreaQuery query) {
        List<AreaEntity> areas = areaService.listSubArea(query);
        // TODO 先根据城市filter再聚合
//        QueryBuilders.boolQuery().filter(
//                QueryBuilders.termQuery()
//        )
        return null;
    }

}
