package com.auberge.gmall.cart.mapper;

import com.auberge.gmall.bean.CartInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CartInfoMapper extends Mapper<CartInfo> {
    /**
     * 根据userId查询实时价格到cartInfo中
     * @param userId
     * @return
     */
    List<CartInfo> selectCartListWithCurPrice(String userId);
}
