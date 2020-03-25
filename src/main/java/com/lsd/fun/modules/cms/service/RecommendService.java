package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.entity.RecommendEntity;
import com.lsd.fun.common.utils.BaseQuery;
import java.util.Map;

/**
 * 用户-推荐商铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
public interface RecommendService extends IService<RecommendEntity> {

    PageUtils queryPage(BaseQuery query);
}

