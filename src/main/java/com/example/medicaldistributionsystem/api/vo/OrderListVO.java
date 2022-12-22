package com.example.medicaldistributionsystem.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 订单列表页面VO
 */
@Data
public class OrderListVO implements Serializable {

    private Long orderId;

    @ApiModelProperty("订单价格")
    private Integer totalPrice;

    @ApiModelProperty("订单状态")
    private String orderStatus;

    @ApiModelProperty("订单项列表")
    private List<OrderItemVO> orderItemVOS;
}
