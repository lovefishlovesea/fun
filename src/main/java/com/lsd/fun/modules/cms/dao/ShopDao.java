package com.lsd.fun.modules.cms.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.lsd.fun.modules.cms.dto.ShopVO;
import com.lsd.fun.modules.cms.entity.ShopEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 01:29:43
 */
@Mapper
public interface ShopDao extends BaseMapper<ShopEntity> {

    List<ShopVO> listAll(@Param(Constants.WRAPPER) Wrapper wrapper);
}
