package com.lsd.fun.modules.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.annotation.AppLogin;
import com.lsd.fun.modules.app.annotation.AppLoginUser;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.service.AppfamilyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 家属用户
 *
 * @author lsd
 * @email syndaliang@foxmail.com
 * @date 2020-01-14 11:30:31
 */
@Api(tags = "App用户模块")
@RestController
@RequestMapping("app/user")
public class AppfamilyUserController {
    @Autowired
    private AppfamilyUserService appfamilyUserService;


    @AppLogin
    @ApiOperation("获取当前用户信息")
    @GetMapping
    public R getUserInfo(@AppLoginUser UserRoleDto userRoleDto){
        return R.ok().put("userRoleDto", userRoleDto);
    }

}
