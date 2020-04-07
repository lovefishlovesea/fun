package com.lsd.fun.modules.app.query;

import com.lsd.fun.common.utils.BaseQuery;
import com.lsd.fun.common.utils.Constant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 搜索参数
 * Created by lsd
 * 2020-04-04 13:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopSearchQuery extends BaseQuery {

    @ApiModelProperty("用户当前经度")
    private double lng;
    @ApiModelProperty("用户当前纬度")
    private double lat;
    @ApiModelProperty("关键字")
    @NotBlank(message = "请输入关键字")
    private String keyword;
    @ApiModelProperty("分类id")
    private Integer categoryId;
    @ApiModelProperty("标签")
    private String tags;

    @ApiModelProperty(value = "0:语义相关性应用于召回策略(模糊模式)，1:语义相关性应用于排序策略(精准模式)", allowableValues = "range[0, 1]", example = "1")
    private Integer isStrict = Constant.TRUE;

}
