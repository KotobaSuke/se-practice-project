package com.example.medicaldistributionsystem.service.impl;

import com.example.medicaldistributionsystem.api.param.SaveCartItemParam;
import com.example.medicaldistributionsystem.api.param.UpdateCartItemParam;
import com.example.medicaldistributionsystem.api.vo.CartItemVO;
import com.example.medicaldistributionsystem.common.Exception;
import com.example.medicaldistributionsystem.common.Constants;
import com.example.medicaldistributionsystem.common.ServiceResultEnum;
import com.example.medicaldistributionsystem.dao.CartItemMapper;
import com.example.medicaldistributionsystem.dao.GoodsMapper;
import com.example.medicaldistributionsystem.entity.CartItem;
import com.example.medicaldistributionsystem.entity.Goods;
import com.example.medicaldistributionsystem.service.CartService;
import com.example.medicaldistributionsystem.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public String saveCartItem(SaveCartItemParam saveCartItemParam, Long userId) {
        CartItem temp = cartItemMapper.selectByUserIdAndGoodsId(userId, saveCartItemParam.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            Exception.fail(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        Goods goods = goodsMapper.selectByPrimaryKey(saveCartItemParam.getGoodsId());
        //商品为空
        if (goods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }

        CartItem cartItem = new CartItem();
        BeanUtil.copyProperties(saveCartItemParam, cartItem);
        cartItem.setUserId(userId);
        //保存记录
        if (cartItemMapper.insertSelective(cartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCartItem(UpdateCartItemParam updateCartItemParam, Long userId) {
        CartItem cartItemUpdate = cartItemMapper.selectByPrimaryKey(updateCartItemParam.getCartItemId());
        if (cartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (!cartItemUpdate.getUserId().equals(userId)) {
            Exception.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }

        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!cartItemUpdate.getUserId().equals(userId)) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (updateCartItemParam.getGoodsCount().equals(cartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        cartItemUpdate.setGoodsCount(updateCartItemParam.getGoodsCount());
        //修改记录
        if (cartItemMapper.updateByPrimaryKeySelective(cartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }



    @Override
    public Boolean deleteById(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemMapper.selectByPrimaryKey(cartItemId);
        if (cartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(cartItem.getUserId())) {
            return false;
        }
        return cartItemMapper.deleteByPrimaryKey(cartItemId) > 0;
    }

    @Override
    public List<CartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long userId) {
        List<CartItemVO> cartItemVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(cartItemIds)) {
            Exception.fail("购物项不能为空");
        }
        List<CartItem> cartItems = cartItemMapper.selectByUserIdAndCartItemIds(userId, cartItemIds);
        if (CollectionUtils.isEmpty(cartItems)) {
            Exception.fail("购物项不能为空");
        }
        if (cartItems.size() != cartItemIds.size()) {
            Exception.fail("参数异常");
        }
        return getCartItemVOS(cartItemVOS, cartItems);
    }

    @Override
    public List<CartItemVO> getCartItems(Long userId) {
        List<CartItemVO> cartItemVOS = new ArrayList<>();
        List<CartItem> cartItems = cartItemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getCartItemVOS(cartItemVOS, cartItems);
    }



    /**
     * 数据转换
     *
     * @param cartItemVOS
     * @param cartItems
     * @return
     */
    private List<CartItemVO> getCartItemVOS(List<CartItemVO> cartItemVOS, List<CartItem> cartItems) {
        if (!CollectionUtils.isEmpty(cartItems)) {
            //查询商品信息并做数据转换
            List<Long> goodsIds = cartItems.stream().map(CartItem::getGoodsId).collect(Collectors.toList());
            List<Goods> goods = goodsMapper.selectByPrimaryKeys(goodsIds);
            Map<Long, Goods> goodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(goods)) {
                goodsMap = goods.stream().collect(Collectors.toMap(Goods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (CartItem cartItem : cartItems) {
                CartItemVO cartItemVO = new CartItemVO();
                BeanUtil.copyProperties(cartItem, cartItemVO);
                if (goodsMap.containsKey(cartItem.getGoodsId())) {
                    Goods goodsTemp = goodsMap.get(cartItem.getGoodsId());
                    String goodsName = goodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    cartItemVO.setGoodsName(goodsName);
                    cartItemVOS.add(cartItemVO);
                }
            }
        }
        return cartItemVOS;
    }

    @Override
    public CartItem getCartItemById(Long cartItemId) {
        CartItem cartItem = cartItemMapper.selectByPrimaryKey(cartItemId);
        if (cartItem == null) {
            Exception.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return cartItem;
    }




}
