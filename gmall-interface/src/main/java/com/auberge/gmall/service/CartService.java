package com.auberge.gmall.service;

import com.auberge.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {
    //写方法？ skuId，SkuNum，userId

    /**
     * 添加购物车
     *
     * @param skuId
     * @param userId
     * @param skuNum
     */
    void addToCart(String skuId, String userId, Integer skuNum);

    /**
     * 获取登录状态下的购物车信息
     *
     * @param userId
     * @return
     */
    List<CartInfo> getCartList(String userId);

    /**
     * 合并购物车
     *
     * @param cartListCK
     * @param userId
     * @return
     */
    List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId);

    /**
     * 修改商品状态
     * @param skuId
     * @param isChecked
     * @param userId
     */
    void checkCart(String skuId, String isChecked, String userId);
}
