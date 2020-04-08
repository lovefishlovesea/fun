package com.lsd.fun.modules.app.controller;

import com.lsd.fun.common.utils.R;
import com.lsd.fun.modules.app.annotation.AppLogin;
import com.lsd.fun.modules.app.annotation.AppLoginUser;
import com.lsd.fun.modules.app.dto.CartDto;
import com.lsd.fun.modules.app.dto.UserRoleDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "App购物车")
@RequestMapping("app/cart")
@RestController
public class AppCartController {

    @Autowired
    private com.lsd.fun.modules.app.service.CartService CartService;

    @AppLogin
    @ApiOperation("查询购物车列表")
    @PostMapping("/list")
    public R list(@AppLoginUser UserRoleDto dto) {
        return R.ok().put(
                "list",
                CartService.list(dto.getUserId().longValue()));
    }

    @AppLogin
    @ApiOperation("新增/更新购物车商品")
    @PostMapping("/save")
    public R save(CartDto dto, @AppLoginUser UserRoleDto userRoleDto) {
        CartService.save(dto, userRoleDto.getUserId().longValue());
        return R.ok();
    }

    @AppLogin
    @ApiOperation("删除购物车商品")
    @DeleteMapping("/delete")
    public R delete(
            @RequestParam @ApiParam("点餐类型，0：食物 1：食谱") Integer type,
            @RequestParam Long goodsId,
            @AppLoginUser UserRoleDto userRoleDto) {
        return CartService.deleteByGoodsId(
                goodsId,
                userRoleDto.getUserId().longValue())
                ? R.ok()
                : R.error("删除失败");
    }
}
