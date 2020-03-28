package com.lsd.fun.modules.cms.dto;

import com.lsd.fun.modules.cms.entity.ShopEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by lsd
 * 2020-03-26 11:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopVO extends ShopEntity {

    // 商铺类别名称
    private String category;
    // 商家名称
    private String seller;
    // 封面url
    private String coverUrl;
    // 封面是否爬取的
    private Integer isCrawl;

}
