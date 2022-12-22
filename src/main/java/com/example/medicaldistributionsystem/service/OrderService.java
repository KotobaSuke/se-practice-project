package com.example.medicaldistributionsystem.service;

import com.example.medicaldistributionsystem.api.vo.CartItemVO;
import com.example.medicaldistributionsystem.api.vo.OrderDetailVO;
import com.example.medicaldistributionsystem.entity.User;
import com.example.medicaldistributionsystem.util.PageQueryUtil;
import com.example.medicaldistributionsystem.util.PageResult;
import java.util.List;

public interface OrderService {

    /**
     * 获取订单详情
     *
     *
     * @param orderId
     * @param userId
     * @return
     */
    OrderDetailVO getOrderDetailByOrderId(Long orderId, Long userId);



    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderId
     * @param userId
     * @return
     */
    String deleteOrder(Integer orderId, Long userId);

    /**
     * 确认收货
     *
     * @param orderId
     * @param userId
     * @return
     */
    String finishOrder(Integer orderId, Long userId);

    String paySuccess(Integer orderId, int payType);

    String saveOrder(User loginUser, List<CartItemVO> itemsForSave);



}
