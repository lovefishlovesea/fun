package com.lsd.fun.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 家属用户
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-01-14 11:30:31
 */
@Accessors(chain = true)
@Data
@TableName("t_family_user")
public class AppfamilyUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 *
	 */
	private String username;
	/**
	 *
	 */
	private String password;
	/**
	 * 盐
	 */
	@JsonIgnore  // 不序列化
	private String salt;
	/**
	 *
	 */
	private String avatar;
	/**
	 *
	 */
	private String email;
	/**
	 *
	 */
	private String mobile;
	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	/**
	 *
	 */
	private LocalDateTime createdAt;
	/**
	 *
	 */
	private LocalDateTime updatedAt;
	/**
	 *
	 */
	private LocalDateTime deletedAt;

}
