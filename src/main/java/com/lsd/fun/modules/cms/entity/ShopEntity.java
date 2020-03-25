package com.lsd.fun.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;

/**
 * 店铺表
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-03-25 21:28:18
 */
@Data
@TableName("shop")
public class ShopEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 商铺标题
	 */
	private String title;
	/**
	 * 商铺介绍
	 */
	private String description;
	/**
	 * 商铺评分
	 */
	private BigDecimal remarkScore;
	/**
	 * 人均消费
	 */
	private Integer pricePerMan;
	/**
	 * 城市标记缩写 如 北京bj
	 */
	private String cityEnName;
	/**
	 * 地区英文简写 如昌平区 cpq
	 */
	private String regionEnName;
	/**
	 * 详细地址
	 */
	private String address;
	/**
	 * 商铺类别id
	 */
	private Integer categoryId;
	/**
	 * 以" "分隔的标签
	 */
	private String tags;
	/**
	 * 创建时间
	 */
	private LocalDateTime createdAt;
	/**
	 * 更新时间
	 */
	private LocalDateTime updatedAt;
	/**
	 * 商家id
	 */
	private Integer sellerId;
	/**
	 * 封面
	 */
	private String iconUrl;

}
