package com.lsd.fun.modules.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.common.utils.Query;
import com.lsd.fun.modules.cms.dao.AreaDao;
import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.lsd.fun.modules.cms.service.AreaService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class AreaServiceImpl extends ServiceImpl<AreaDao, AreaEntity> implements AreaService {

    @Override
    public PageUtils queryPage(BaseQuery query) {
        IPage<AreaEntity> page = this.page(
                new Query<AreaEntity>().getPage(query),
                new QueryWrapper<AreaEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AreaEntity> listTree() {
        List<AreaEntity> areas = this.list();
        if (CollectionUtils.isEmpty(areas)) {
            return areas;
        }
        Map<Integer, List<AreaEntity>> groupByPid = areas.stream()
                .filter(area -> area.getLevel() > 0)
                .collect(Collectors.groupingBy(AreaEntity::getPid));
        return areas.stream()
                .map(area -> {
                    List<AreaEntity> subAreas = groupByPid.get(area.getId());
                    return area.setSubAreas(subAreas);
                })
                .filter(area -> area.getLevel() == 0)
                .collect(Collectors.toList());
    }


}
