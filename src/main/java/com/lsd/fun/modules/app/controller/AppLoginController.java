package com.lsd.fun.modules.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.Constant;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.common.validator.ValidatorUtils;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import com.lsd.fun.modules.app.entity.AppfamilyUserEntity;
import com.lsd.fun.modules.app.form.AppLoginForm;
import com.lsd.fun.modules.app.form.AppRegisterForm;
import com.lsd.fun.modules.app.service.AppfamilyUserService;
import com.lsd.fun.modules.app.utils.JwtUtils;
import com.lsd.fun.modules.sys.entity.SysUserEntity;
import com.lsd.fun.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by lsd
 * 2020-01-13 15:38
 */
@Api(tags = "App登录接口")
@RequestMapping("/app")
@RestController
public class AppLoginController {

    private final SysUserService SysUserService;
    private static SysUserService sysUserService;
    private final AppfamilyUserService AppfamilyUserService;
    private static AppfamilyUserService appfamilyUserService;
    private final JwtUtils jwtUtils;

    public AppLoginController(JwtUtils jwtUtils, AppfamilyUserService appfamilyUserService, SysUserService sysUserService) {
        this.jwtUtils = jwtUtils;
        this.AppfamilyUserService = appfamilyUserService;
        this.SysUserService = sysUserService;
    }

    @PostConstruct
    public void init() {
        sysUserService = SysUserService;
        appfamilyUserService = AppfamilyUserService;
    }

    @ApiOperation("手机号是否已被使用")
    @PostMapping("/check-mobile")
    public R checkMobile(@RequestParam String mobile, @RequestParam @ApiParam("0：员工，1：家属") Integer userType) {
        final boolean mobileExist = AppRegisterLoginHandler.values()[userType].isMobileExist(mobile);
        return R.ok().put("exist", mobileExist);
    }

    @ApiOperation("用户名是否已被使用")
    @PostMapping("/check-username")
    public R checkUsername(@RequestParam String username, @RequestParam @ApiParam("0：员工，1：家属") Integer userType) {
        final boolean usernameExist = AppRegisterLoginHandler.values()[userType].isUsernameExist(username);
        return R.ok().put("exist", usernameExist);
    }


    @ApiOperation("移动端用户注册")
    @PostMapping("/register")
    public R register(@RequestBody AppRegisterForm form) {
        AppRegisterLoginHandler.values()[form.getUserType()].register(form);
        return R.ok();
    }

    @ApiOperation("移动端登录")
    @PostMapping("/login")
    public R login(@RequestBody AppLoginForm form) {
        ValidatorUtils.validateEntity(form);
        final var appUserRoleDto = AppRegisterLoginHandler.values()[form.getUserType()].login(form);
        // 发放token
        return R.ok().put("token", jwtUtils.generateToken(appUserRoleDto));
    }

    private static void checkPassword(String password, String salt, String formPassword) {
        if (!StringUtils.equals(password, new Sha256Hash(formPassword, salt).toHex())) {
            throw new RRException("用户名密码错误", HttpStatus.UNAUTHORIZED.value());
        }
    }

    private static void checkStatus(Integer status) {
        if (status.equals(Constant.FALSE)) {
            throw new RRException("用户被禁用", HttpStatus.FORBIDDEN.value());
        }
    }

    enum AppRegisterLoginHandler {
        EMPLOYEE_USER {
            @Override
            public boolean isUsernameExist(String username) {
                return sysUserService.lambdaQuery().eq(SysUserEntity::getUsername, username).one() != null;
            }

            @Override
            public boolean isMobileExist(String mobile) {
                return sysUserService.lambdaQuery().eq(SysUserEntity::getMobile, mobile).one() != null;
            }

            @Override
            public void register(AppRegisterForm form) {
                //sha256加密
                String salt = RandomStringUtils.randomAlphanumeric(20);
                final var sysUserEntity = SysUserEntity.builder()
                        .username(form.getUsername())
                        .password(new Sha256Hash(form.getPassword(), salt).toHex())
                        .salt(salt)
                        .mobile(form.getMobile())
                        .status(Constant.TRUE)
                        .createTime(LocalDateTime.now())
                        .build();
                try {
                    sysUserService.save(sysUserEntity);
                } catch (org.springframework.dao.DuplicateKeyException e) {
                    throw new RRException("用户名或手机号已存在");
                }
            }

            @Override
            public UserRoleDto login(AppLoginForm form) {
                final var sysUser = Optional.ofNullable(sysUserService.queryByUserName(form.getUsername()))
                        .orElseThrow(() -> new RRException("用户名密码错误", HttpStatus.UNAUTHORIZED.value()));
                checkPassword(sysUser.getPassword(), sysUser.getSalt(), form.getPassword());
                checkStatus(sysUser.getStatus());
                final var roles = Optional.ofNullable(sysUserService.queryAllRoles(sysUser.getUserId())).orElse(List.of());
                return UserRoleDto.builder().type(form.getUserType()).userId(sysUser.getUserId()).roleList(roles).build();
            }
        },

        FAMILY_USER {
            @Override
            public boolean isUsernameExist(String username) {
                return appfamilyUserService.lambdaQuery().eq(AppfamilyUserEntity::getUsername, username).one() != null;
            }

            @Override
            public boolean isMobileExist(String mobile) {
                return appfamilyUserService.lambdaQuery().eq(AppfamilyUserEntity::getMobile, mobile).one() != null;
            }

            @Override
            public void register(AppRegisterForm form) {
                //sha256加密
                String salt = RandomStringUtils.randomAlphanumeric(20);
                final var familyUserEntity = new AppfamilyUserEntity()
                        .setUsername(form.getUsername())
                        .setPassword(new Sha256Hash(form.getPassword(), salt).toHex())
                        .setSalt(salt)
                        .setMobile(form.getMobile())
                        .setStatus(Constant.TRUE)
                        .setCreatedAt(LocalDateTime.now());
                try {
                    appfamilyUserService.save(familyUserEntity);
                } catch (org.springframework.dao.DuplicateKeyException e) {
                    throw new RRException("用户名或手机号已存在");
                }
            }

            @Override
            public UserRoleDto login(AppLoginForm form) {
                final var familyUser = Optional.ofNullable(appfamilyUserService.lambdaQuery().eq(AppfamilyUserEntity::getUsername, form.getUsername()).one())
                        .orElseThrow(() -> new RRException("用户名密码错误", HttpStatus.UNAUTHORIZED.value()));
                checkPassword(familyUser.getPassword(), familyUser.getSalt(), form.getPassword());
                checkStatus(familyUser.getStatus());
                return UserRoleDto.builder().type(form.getUserType()).userId(familyUser.getId()).roleList(List.of(FAMILY_USER.toString())).build();
            }
        };

        public abstract boolean isUsernameExist(String username);
        public abstract boolean isMobileExist(String mobile);
        public abstract void register(AppRegisterForm form);
        public abstract UserRoleDto login(AppLoginForm form);
    }

}
