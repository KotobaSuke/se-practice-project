package com.example.medicaldistributionsystem.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单详情页页面VO
 */
@Data
public class OrderDetailVO implements Serializable {

    @ApiModelProperty("订单的id")
    private Integer orderId;

    @ApiModelProperty("订单价格")
    private Integer totalPrice;

    @ApiModelProperty("订单状态")
    private String orderStatus;

    @ApiModelProperty("配送员的id")
    private Integer deliveryId;

    @ApiModelProperty("用户的id")
    private Integer userId;

    @ApiModelProperty("时间")
    private Date orderTime;

    @ApiModelProperty("地址")
    private Date orderAddress;

    @ApiModelProperty("订单项列表")
    private List<OrderItemVO> orderItemVOS;
}
