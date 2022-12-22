package com.example.medicaldistributionsystem.entity;

import lombok.Data;



@Data
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private String goodsName;
}