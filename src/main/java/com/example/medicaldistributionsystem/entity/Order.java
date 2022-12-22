package com.example.medicaldistributionsystem.entity;


import lombok.Data;
import java.util.Date;

@Data
public class Order {
    private Long orderId;

    private Integer deliveryId;

    private Long userId;

    private Integer totalPrice;

    private String orderAddress;

    private Date orderTime;

    private String orderStatus;


}
