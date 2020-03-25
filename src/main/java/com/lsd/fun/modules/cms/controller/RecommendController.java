package com.lsd.fun.modules.cms.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.entity.RecommendEntity;
import com.lsd.fun.modules.cms.service.RecommendService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.R;



/**
 * 用户-推荐商铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@RestController
@RequestMapping("cms/recommend")
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("cms:recommend:list")
    public R list(BaseQuery query){
        PageUtils page = recommendService.queryPage(query);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{memberId}")
    @RequiresPermissions("cms:recommend:info")
    public R info(@PathVariable("memberId") Integer memberId){
		RecommendEntity recommend = recommendService.getById(memberId);

        return R.ok().put("recommend", recommend);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("cms:recommend:save")
    public R save(@RequestBody RecommendEntity recommend){
		recommendService.save(recommend);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("cms:recommend:update")
    public R update(@RequestBody RecommendEntity recommend){
		recommendService.updateById(recommend);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("cms:recommend:delete")
    public R delete(@RequestBody Integer[] memberIds){
		recommendService.removeByIds(Arrays.asList(memberIds));

        return R.ok();
    }

}
