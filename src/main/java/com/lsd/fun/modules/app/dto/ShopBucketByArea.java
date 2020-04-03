package com.lsd.fun.modules.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据地区聚合商铺信息DTO
 *
 * Created by lsd
 * 2020-04-03 10:15
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopBucketByArea {

    /**
     * 聚合bucket的key
     */
    private String key;

    /**
     * 聚合结果值
     */
    private long count;

}
