package com.example.medicaldistributionsystem.entity;

import lombok.Data;


@Data
public class CartItem {
    private Long cartItemId;

    private Long userId;

    private Long goodsId;

    private Integer goodsCount;

    private String goodsName;

    private Integer goodsPrice;


}