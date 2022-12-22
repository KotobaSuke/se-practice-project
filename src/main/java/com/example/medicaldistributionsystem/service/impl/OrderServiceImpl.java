package com.example.medicaldistributionsystem.service.impl;

import com.example.medicaldistributionsystem.common.Exception;
import com.example.medicaldistributionsystem.entity.*;
import com.example.medicaldistributionsystem.service.*;
import com.example.medicaldistributionsystem.dao.*;
import com.example.medicaldistributionsystem.common.*;
import com.example.medicaldistributionsystem.api.vo.*;
import com.example.medicaldistributionsystem.util.BeanUtil;
import com.example.medicaldistributionsystem.util.PageQueryUtil;
import com.example.medicaldistributionsystem.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;

public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private GoodsMapper goodsMapper;




    @Override
    public OrderDetailVO getOrderDetailByOrderId(Long orderId, Long userId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            List<OrderItemVO> orderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
            OrderDetailVO orderDetailVO = new OrderDetailVO();
            BeanUtil.copyProperties(order, orderDetailVO);
            orderDetailVO.setOrderItemVOS(orderItemVOS);
            return orderDetailVO;
        } else {
            Exception.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }




    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = orderMapper.getTotalOrders(pageUtil);
        List<Order> orders = orderMapper.findOrderList(pageUtil);
        List<OrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(orders, OrderListVO.class);
            //设置订单状态中文显示值

            List<Long> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<OrderItem> orderItems = orderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(OrderItem::getOrderId));
                for (OrderListVO orderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(orderListVO.getOrderId())) {
                        List<OrderItem> orderItemListTemp = itemByOrderIdMap.get(orderListVO.getOrderId());
                        //将OrderItem对象列表转换成OrderItemVO对象列表
                        List<OrderItemVO> orderItemVOS = BeanUtil.copyList(orderItemListTemp, OrderItemVO.class);
                        orderListVO.setOrderItemVOS(orderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }



    @Override
    public String deleteOrder(Integer orderId, Long userId) {
        Order order =  orderMapper.selectByOrderId(orderId);
        if (order != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(order.getUserId())) {
                Exception.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }

            if (orderMapper.deleteOrder(Collections.singletonList(order.getOrderId()))>0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(Integer orderId, Long userId) {
        Order order =  orderMapper.selectByOrderId(orderId);
        if (order != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(order.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }

            order.setOrderStatus("received");

            if (orderMapper.updateByPrimaryKeySelective(order) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(Integer orderId, int payType) {
        Order order = orderMapper.selectByOrderId(orderId);
        if (order != null) {

            order.setOrderStatus("paid");


            order.setOrderTime(new Date());

            if (orderMapper.updateByPrimaryKeySelective(order) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(User loginUser, List<CartItemVO> cartItems) {
        List<Long> itemIdList = cartItems.stream().map(CartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = cartItems.stream().map(CartItemVO::getGoodsId).collect(Collectors.toList());
        List<Goods> goods = goodsMapper.selectByPrimaryKeys(goodsIds);

        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goods)) {
            if (cartItemMapper.deleteBatch(itemIdList) > 0) {



                int priceTotal = 0;
                //保存订单
                Order order = new Order();
                order.setUserId(loginUser.getUserId());
                //总价
                for (CartItemVO cartItemVO : cartItems) {
                    priceTotal += cartItemVO.getGoodsCount() * cartItemVO.getGoodsPrice();
                }
                if (priceTotal < 1) {
                    Exception.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                order.setTotalPrice(priceTotal);

                //生成订单项并保存订单项纪录
                if (orderMapper.insertSelective(order) > 0) {

                    //生成所有的订单项快照，并保存至数据库
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (CartItemVO cartItemVO : cartItems) {
                        OrderItem orderItem = new OrderItem();
                        //使用BeanUtil工具类将CartItemVO中的属性复制到OrderItem对象中
                        BeanUtil.copyProperties(cartItemVO, orderItem);
                        //OrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        orderItem.setOrderId(order.getOrderId());
                        orderItems.add(orderItem);
                    }
                    //保存至数据库
                    orderItemMapper.insertBatch(orderItems);


                    Exception.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                Exception.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            Exception.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        Exception.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }













}

