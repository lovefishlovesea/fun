package com.lsd.fun.modules.app.form;

import io.swagger.annotations.Api;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Created by lsd
 * 2020-01-15 14:45
 */
@EqualsAndHashCode(callSuper = true)
@Api(tags = "App用户注册表单")
@Data
public class AppRegisterForm extends AppLoginForm{

    @NotNull(message = "请输入手机号")
    private String mobile;

}
