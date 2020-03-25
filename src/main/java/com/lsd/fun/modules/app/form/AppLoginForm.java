package com.lsd.fun.modules.app.form;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by lsd
 * 2020-01-14 14:05
 */
@Api(tags = "App用户登录表单")
@Data
public class AppLoginForm {

    @NotNull(message = "请输入用户名")
    private String username;

    @NotNull(message = "请输入密码")
    private String password;

    @NotNull(message = "请先选择用户类型")
    @ApiModelProperty(value = "用户类型（0：员工，1：家属）")
    private Integer userType;

}
