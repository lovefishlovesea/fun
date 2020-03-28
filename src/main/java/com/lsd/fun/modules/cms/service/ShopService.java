package com.lsd.fun.modules.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsd.fun.common.utils.PageUtils;
import com.lsd.fun.modules.cms.controller.ShopQuery;
import com.lsd.fun.modules.cms.dto.ShopExcelDTO;
import com.lsd.fun.modules.cms.dto.ShopVO;
import com.lsd.fun.modules.cms.entity.ShopEntity;
import com.lsd.fun.common.utils.BaseQuery;

import java.util.List;
import java.util.Map;

/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-26 01:29:43
 */
public interface ShopService extends IService<ShopEntity> {

    PageUtils queryPage(ShopQuery query);

    /**
     * ShopExcelDTO -> ShopEntity 批量新增或更新
     *
     * @param parsedResult    从Excel数据解析出来的数据
     */
    void saveFromExcelParsedResult(List<ShopExcelDTO> parsedResult, Integer isUpdate);

    List<ShopVO> queryAll();

    ShopVO queryById(Integer id);

    void update(ShopEntity shop);

    void removeLogic(List<Integer> idList);
}

