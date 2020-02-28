package com.auberge.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.auberge.gmall.bean.CartInfo;
import com.auberge.gmall.bean.SkuInfo;
import com.auberge.gmall.config.CookieUtil;
import com.auberge.gmall.service.ManageService;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartCookieHandler {
    // 定义购物车名称
    private String cookieCartName = "CART";
    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE = 7 * 24 * 3600;
    @Reference
    private ManageService manageService;

    /**
     * 添加购物车
     * @param request
     * @param response
     * @param skuId
     * @param userId
     * @param skuNum
     */
    public void addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, Integer skuNum) {
        /*
            1. 查看购物车中是否有该商品
            2. true：数量相加
            3. false：直接添加
         */
        //从cookie中获取购物车数据
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        System.out.println(cookieValue);
        List<CartInfo> cartInfoList=new ArrayList<>();
        boolean isExist=false;
        //该字符串中包含很多和cartInfo实体类
        if (StringUtils.isNoneEmpty(cookieValue)){
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            for (CartInfo cartInfo : cartInfoList) {
                //比较条件为商品Id
                if (cartInfo.getSkuId().equals(skuId)){
                    //有商品
                    cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
                    //实时价格初始化
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    //将变量更改为true
                    isExist=true;
                }
            }
        }
        //购物车中没有该商品
        if (!isExist){
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            //将商品添加到集合
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuInfo.getId());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfoList.add(cartInfo);
        }
        //将集合放入cookie中
        System.out.println(cartInfoList);
        CookieUtil.setCookie(request,response,cookieCartName,JSON.toJSONString(cartInfoList),COOKIE_CART_MAXAGE,true);
    }

    public List<CartInfo> getCartList(HttpServletRequest request) {
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        if (cookieValue!=null){
            List<CartInfo> cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            return cartInfoList;
        }
        return null;
    }

    /**
     * 删除购物车
     * @param request
     * @param response
     */
    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,cookieCartName);
    }

    /**
     * 
     * @param request
     * @param response
     * @param skuId
     * @param isChecked
     */
    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        //直接将isChecked赋给购物车集合
        List<CartInfo> cartList = getCartList(request);
        if (cartList!=null&&cartList.size()>0){
            for (CartInfo cartInfo : cartList) {
                if (cartInfo.getSkuId().equals(skuId)){
                    cartInfo.setIsChecked(isChecked);
                }
            }
        }
        //购物车集合写回cookie
        CookieUtil.setCookie(request,response,cookieCartName, JSON.toJSONString(cartList),COOKIE_CART_MAXAGE,true);
    }
}
