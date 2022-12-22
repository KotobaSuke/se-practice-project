package com.example.medicaldistributionsystem.dao;

import com.example.medicaldistributionsystem.entity.CartItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(CartItem record);

    int insertSelective(CartItem record);

    CartItem selectByPrimaryKey(Long cartItemId);

    CartItem selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    List<CartItem> selectByUserId(@Param("userId") Long userId, @Param("number") int number);

    int updateByPrimaryKeySelective(CartItem record);

    int deleteBatch(List<Long> ids);
    List<CartItem> selectByUserIdAndCartItemIds(@Param("userId") Long userId, @Param("cartItemIds") List<Long> cartItemIds);


}