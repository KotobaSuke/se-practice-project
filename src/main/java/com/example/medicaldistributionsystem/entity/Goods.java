package com.example.medicaldistributionsystem.entity;


import lombok.Data;



@Data
public class Goods {
    private Long goodsId;
    private String goodsName;
    private Integer goodsPrice;
    private String goodsDetailContent;


}