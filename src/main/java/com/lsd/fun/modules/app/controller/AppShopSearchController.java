package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.dto.ShopBucketByArea;
import com.lsd.fun.modules.app.service.ShopSearchService;
import com.lsd.fun.modules.cms.query.AreaQuery;
import com.lsd.fun.modules.cms.service.AreaService;
import com.lsd.fun.modules.cms.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by lsd
 * 2020-04-02 17:19
 */
@Api(tags = "App商铺搜索服务")
@RestController
@RequestMapping("app/shop")
public class AppShopSearchController {

    @Autowired
    private ShopSearchService shopSearchService;

    @ApiOperation(value = "根据选定城市聚合子地区商铺信息")
    @GetMapping("/area")
    public R list(AreaQuery query) {
        List<ShopBucketByArea> aggResult = shopSearchService.aggBySubArea(query);
        return R.ok().put("data", aggResult);
    }

}
