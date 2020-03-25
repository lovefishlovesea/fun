package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.ShopEntity;
import com.lsd.fun.modules.cms.service.ShopService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:shop:list")
    public R list(BaseQuery query){
        PageUtils page = shopService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("cms:shop:info")
    public R info(@PathVariable("id") Integer id){
		ShopEntity shop = shopService.getById(id);

        return R.ok().put("shop", shop);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:shop:save")
    public R save(@RequestBody ShopEntity shop){
		shopService.save(shop);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:shop:update")
    public R update(@RequestBody ShopEntity shop){
		shopService.updateById(shop);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:shop:delete")
    public R delete(@RequestBody Integer[] ids){
		shopService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
