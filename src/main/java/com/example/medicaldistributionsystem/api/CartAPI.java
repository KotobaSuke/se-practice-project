package com.example.medicaldistributionsystem.api;

import com.example.medicaldistributionsystem.api.param.SaveCartItemParam;
import com.example.medicaldistributionsystem.api.param.UpdateCartItemParam;
import com.example.medicaldistributionsystem.api.vo.CartItemVO;
import com.example.medicaldistributionsystem.common.ServiceResultEnum;
import com.example.medicaldistributionsystem.config.annotation.TokenToUser;
import com.example.medicaldistributionsystem.entity.CartItem;
import com.example.medicaldistributionsystem.entity.User;
import com.example.medicaldistributionsystem.service.CartService;
import com.example.medicaldistributionsystem.util.Result;
import com.example.medicaldistributionsystem.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "v1", tags = "购物车相关接口")
@RequestMapping("/api/v1")
public class CartAPI {

    @Resource
    private CartService cartService;
    //查看购物车内的医药商品
    @GetMapping("/shop-cart")
    @ApiOperation(value = "购物车列表", notes = "")
    public Result<List<CartItemVO>> cartItemList(@TokenToUser User loginUser) {
        return ResultGenerator.genSuccessResult(cartService.getCartItems(loginUser.getUserId()));
    }

    @PostMapping("/shop-cart")
    @ApiOperation(value = "添加商品到购物车接口", notes = "传参为商品id、数量")
    public Result CartItem(@RequestBody SaveCartItemParam saveCartItemParam,
                           @TokenToUser User loginUser) {
        String saveResult = cartService.saveCartItem(saveCartItemParam, loginUser.getUserId());
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ApiOperation(value = "修改购物项数据", notes = "传参为购物项id、数量")
    public Result updateCartItem(@RequestBody UpdateCartItemParam updateCartItemParam,
                                 @TokenToUser User loginUser) {
        String updateResult = cartService.updateCartItem(updateCartItemParam, loginUser.getUserId());
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{CartItemId}")
    @ApiOperation(value = "删除购物项", notes = "传参为购物项id")
    public Result updateCartItem(@PathVariable("CartItemId") Long cartItemId,
                                 @TokenToUser User loginUser) {
        CartItem cartItemById = cartService.getCartItemById(cartItemId);
        if (!loginUser.getUserId().equals(cartItemById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = cartService.deleteById(cartItemId, loginUser.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }


}