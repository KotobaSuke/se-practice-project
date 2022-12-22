package com.example.medicaldistributionsystem.dao;

import com.example.medicaldistributionsystem.entity.Goods;
import java.util.List;

public interface GoodsMapper {


    Goods selectByPrimaryKey(Long goodsId);
    List<Goods> selectByPrimaryKeys(List<Long> goodsIds);



}