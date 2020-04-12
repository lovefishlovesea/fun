package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.modules.app.query.MapSearchQuery;
import com.lsd.fun.modules.app.query.ShopSearchQuery;
import com.lsd.fun.modules.app.vo.ShopBucketByArea;
import com.lsd.fun.modules.app.service.ShopSearchService;
import com.lsd.fun.modules.app.vo.ShopSearchResult;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import com.lsd.fun.modules.cms.service.ShopService;
import com.lsd.fun.modules.cms.vo.ShopVO;
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
    @Autowired
    private ShopService shopService;
    @Autowired
    private BaiduLBSService baiduLBSService;

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


    @ApiOperation("搜索服务")
    @GetMapping("/search")
    public R search(ShopSearchQuery query) {
        ValidatorUtils.validateEntity(query);
        ShopSearchResult result = shopSearchService.search(query);
        return R.ok().put("data", result).put("more", result.getTotal() > (query.getPage() + query.getLimit()));
    }

    @ApiOperation("搜索输入自动补全提示")
    @GetMapping("/search-as-you-type")
    public R searchAsUType(@RequestParam(value = "prefix") String prefix) {
//        if (StringUtils.isBlank(prefix)) {
//            return R.error(HttpStatus.SC_BAD_REQUEST, "输入字符不能为空");
//        }
        List<String> result = shopSearchService.searchAsUType(prefix);
        return R.ok().put("data", result);
    }


    @ApiOperation("查看商品详情")
    @GetMapping("/{id}")
    public R save(@PathVariable("id") Integer id) {
        ShopVO shop = shopService.queryById(id);
        BaiduMapLocation location = baiduLBSService.parseAddress2Location(shop.getProvince() + shop.getCity() + shop.getRegion() + shop.getAddress());
        shop.setLat(location.getLatitude());
        shop.setLng(location.getLongitude());
        return R.ok().put("data", shop);
    }

}
