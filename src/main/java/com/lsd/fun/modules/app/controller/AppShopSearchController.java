package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.modules.app.query.MapSearchQuery;
import com.lsd.fun.modules.app.vo.ShopBucketByArea;
import com.lsd.fun.modules.app.service.ShopSearchService;
import com.lsd.fun.modules.cms.dto.ShopVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public R list(String cityName) {
        if (StringUtils.isBlank(cityName)) {
            return R.error(HttpStatus.SC_BAD_REQUEST, "市级行政单位不能为空");
        }
        List<ShopBucketByArea> aggResult = shopSearchService.aggBySubArea(cityName);
        return R.ok().put("data", aggResult);
    }

    @ApiOperation(value = "根据地图缩放级别查询地图当前视野边界范围内的房源", response = ShopVO.class, responseContainer = "List")
    @GetMapping("/map-houses")
    public R mapSearchHouse(MapSearchQuery query) {
        ValidatorUtils.validateEntity(query);
        // 如果缩放级别小于12则查询整个城市的房源
        PageUtils pageUtils;
        if (query.getLevel() < 12) {
            pageUtils = shopSearchService.mapSearchByCity(query);
        } else {
            // 放大后的地图查询必须要传递当前地图视野的边界参数
            pageUtils = shopSearchService.mapSearchByBound(query);
        }
        boolean more = pageUtils.getTotalCount() > (query.getPage() + query.getLimit());
        return R.ok().put("data", pageUtils).put("more", more);
    }

}
