package com.example.medicaldistributionsystem.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单详情页页面订单项VO
 */
@Data
public class OrderItemVO implements Serializable {

    @ApiModelProperty("商品名称")
    private String goodsName;

}