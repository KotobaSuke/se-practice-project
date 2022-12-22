package com.example.medicaldistributionsystem.dao;

import com.example.medicaldistributionsystem.entity.Order;
import com.example.medicaldistributionsystem.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface OrderMapper {


    int insertSelective(Order record);

    Order selectByPrimaryKey(Long orderId);

    Order selectByOrderId(Integer orderId);

    int updateByPrimaryKeySelective(Order record);

    List<Order> findOrderList(PageQueryUtil pageUtil);

    int getTotalOrders(PageQueryUtil pageUtil);


    int deleteOrder(@Param("orderIds") List<Long> orderIds);


}