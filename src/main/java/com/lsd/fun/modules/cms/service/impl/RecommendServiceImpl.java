package com.lsd.fun.modules.cms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.modules.cms.dao.RecommendDao;
import com.lsd.fun.modules.cms.entity.RecommendEntity;
import com.lsd.fun.modules.cms.service.RecommendService;


@Service
public class RecommendServiceImpl extends ServiceImpl<RecommendDao, RecommendEntity> implements RecommendService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<RecommendEntity> page = this.page(
                new Query<RecommendEntity>().getPage(query),
                new QueryWrapper<RecommendEntity>()
        );

        return new PageUtils(page);
    }

}
