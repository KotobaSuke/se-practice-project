package com.example.medicaldistributionsystem.dao;

import org.apache.ibatis.annotations.Param;
import com.example.medicaldistributionsystem.entity.OrderItem;
import java.util.List;

public interface OrderItemMapper {


    int insert(OrderItem record);


    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<OrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<OrderItem> orderItems);


}