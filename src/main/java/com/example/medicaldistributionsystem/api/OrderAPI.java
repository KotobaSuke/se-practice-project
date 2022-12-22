package com.example.medicaldistributionsystem.api;

import com.example.medicaldistributionsystem.api.param.SaveOrderParam;
import com.example.medicaldistributionsystem.api.vo.CartItemVO;
import com.example.medicaldistributionsystem.api.vo.OrderDetailVO;
import com.example.medicaldistributionsystem.api.vo.OrderListVO;
import com.example.medicaldistributionsystem.common.Constants;
import com.example.medicaldistributionsystem.common.ServiceResultEnum;
import com.example.medicaldistributionsystem.config.annotation.TokenToUser;
import com.example.medicaldistributionsystem.entity.User;
import com.example.medicaldistributionsystem.service.CartService;
import com.example.medicaldistributionsystem.service.OrderService;
import com.example.medicaldistributionsystem.util.PageQueryUtil;
import com.example.medicaldistributionsystem.util.PageResult;
import com.example.medicaldistributionsystem.util.Result;
import com.example.medicaldistributionsystem.common.Exception;
import com.example.medicaldistributionsystem.util.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "订单操作相关接口")
@RequestMapping("/api/v1")
public class OrderAPI {

    @Resource
    private CartService cartService;
    @Resource
    private OrderService orderService;


    @PostMapping("/saveOrder")
    @ApiOperation(value = "生成订单接口", notes = "传参为地址id和待结算的购物项id数组")
    public Result<String> saveOrder(@ApiParam(value = "订单参数") @RequestBody SaveOrderParam saveOrderParam, @TokenToUser User loginUser) {
        int priceTotal = 0;
        if (saveOrderParam == null || saveOrderParam.getCartItemIds() == null || saveOrderParam.getOrderAddress() == null) {
            Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (saveOrderParam.getCartItemIds().length < 1) {
            Exception.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        List<CartItemVO> itemsForSave = cartService.getCartItemsForSettle(Arrays.asList(saveOrderParam.getCartItemIds()), loginUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSave)) {
            //无数据
            Exception.fail("参数异常");
        } else {
            //总价
            for (CartItemVO cartItemVO : itemsForSave) {
                priceTotal += cartItemVO.getGoodsCount() * cartItemVO.getGoodsPrice();
            }
            if (priceTotal < 1) {
                Exception.fail("价格异常");
            }


            //保存订单并返回订单号
            String saveOrderResult = orderService.saveOrder(loginUser, itemsForSave);
            Result result = ResultGenerator.genSuccessResult();
            result.setData(saveOrderResult);
            return result;
        }
        return ResultGenerator.genFailResult("生成订单失败");
    }

    @GetMapping("/order/{orderId}")
    @ApiOperation(value = "订单详情接口", notes = "传参为订单的id")
    public Result<OrderDetailVO> orderDetailPage(@ApiParam(value = "订单的id") @PathVariable("orderId") Long orderId, @TokenToUser User loginUser) {
        return ResultGenerator.genSuccessResult(orderService.getOrderDetailByOrderId(orderId, loginUser.getUserId()));
    }

    @GetMapping("/order")
    @ApiOperation(value = "订单列表接口", notes = "传参为页码")
    public Result<PageResult<List<OrderListVO>>> orderList(@ApiParam(value = "页码") @RequestParam(required = false) Integer pageNumber,
                                                           @ApiParam(value = "订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功") @RequestParam(required = false) Integer status,
                                                           @TokenToUser User loginUser) {
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginUser.getUserId());
        params.put("orderStatus", status);
        params.put("page", pageNumber);
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(orderService.getMyOrders(pageUtil));
    }

    @PutMapping("/order/{orderNo}/delete")
    @ApiOperation(value = "订单取消接口", notes = "传参为订单的id")
    public Result deleteOrder(@ApiParam(value = "订单的id") @PathVariable("orderId") Integer orderId, @TokenToUser User loginUser) {
        String deleteOrderResult = orderService.deleteOrder(orderId, loginUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(deleteOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(deleteOrderResult);
        }
    }

    @PutMapping("/order/{orderNo}/finish")
    @ApiOperation(value = "确认收货接口", notes = "传参为订单号")
    public Result finishOrder(@ApiParam(value = "订单的id") @PathVariable("orderId") Integer orderId, @TokenToUser User loginUser) {
        String finishOrderResult = orderService.finishOrder(orderId, loginUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/paySuccess")
    @ApiOperation(value = "模拟支付成功回调的接口", notes = "传参为订单号和支付方式")
    public Result paySuccess(@ApiParam(value = "订单号") @RequestParam("orderId") Integer orderId, @ApiParam(value = "支付方式") @RequestParam("payType") int payType) {
        String payResult = orderService.paySuccess(orderId, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}