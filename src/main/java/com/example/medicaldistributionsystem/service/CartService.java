package com.example.medicaldistributionsystem.service;

import com.example.medicaldistributionsystem.api.param.SaveCartItemParam;
import com.example.medicaldistributionsystem.api.param.UpdateCartItemParam;
import com.example.medicaldistributionsystem.api.vo.CartItemVO;
import com.example.medicaldistributionsystem.entity.CartItem;
import java.util.List;

public interface CartService {

    /**
     * 保存商品至购物车中
     *
     * @param saveCartItemParam
     * @param userId
     * @return
     */
    String saveCartItem(SaveCartItemParam saveCartItemParam, Long userId);

    /**
     * 修改购物车中的属性
     *
     * @param updateCartItemParam
     * @param userId
     * @return
     */
    String updateCartItem(UpdateCartItemParam updateCartItemParam, Long userId);



    /**
     * 删除购物车中的商品
     *
     *
     * @param cartItemId
     * @param userId
     * @return
     */
    Boolean deleteById(Long cartItemId, Long userId);

    /**
     * 获取购物车中的列表数据
     *
     * @param userId
     * @return
     */
    List<CartItemVO> getCartItems(Long userId);

    /**
     * 获取购物项详情
     *
     * @param cartItemId
     * @return
     */
    CartItem getCartItemById(Long cartItemId);

    /**
     * 根据userId和cartItemIds获取对应的购物项记录
     *
     * @param cartItemIds
     * @param userId
     * @return
     */
    List<CartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long userId);


}