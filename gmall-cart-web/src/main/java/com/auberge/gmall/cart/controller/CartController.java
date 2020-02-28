package com.auberge.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.auberge.gmall.bean.CartInfo;
import com.auberge.gmall.config.LoginRequire;
import com.auberge.gmall.service.CartService;
import com.auberge.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CartController {
    @Reference
    private CartService cartService;
    @Reference
    private ManageService manageService;
    @Resource
    private CartCookieHandler cartCookieHandler;

    @RequestMapping("cartList.html")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request,HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfoList = null;
        if (userId != null) {
            List<CartInfo> cartListCK = cartCookieHandler.getCartList(request);
            if (cartListCK != null && cartListCK.size() > 0) {
                cartInfoList = cartService.mergeToCartList(cartListCK, userId);
                cartCookieHandler.deleteCartCookie(request,response);
            } else {
                cartInfoList = cartService.getCartList(userId);
            }
        } else {
            cartInfoList = cartCookieHandler.getCartList(request);
        }
        request.setAttribute("cartInfoList", cartInfoList);
        return "cartList";
    }

    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response) {
        //获取用户Id
        String userId = (String) request.getAttribute("userId");
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");

        if (userId != null) {
            //调用登录添加购物车
            cartService.addToCart(skuId, userId, Integer.parseInt(skuNum));
        } else {
            //调用未登录购物车
            cartCookieHandler.addToCart(request, response, skuId, userId, Integer.parseInt(skuNum));
        }
        //根据skuId查询skuInfo
        request.setAttribute("skuNum", skuNum);
        request.setAttribute("skuInfo", manageService.getSkuInfo(skuId));
        return "success";
    }

    @RequestMapping("checkCart")
    @LoginRequire
    @ResponseBody
    public void checkCart(HttpServletRequest request,HttpServletResponse response){
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        String userId = (String) request.getAttribute("userId");
        if (userId!=null){
            cartService.checkCart(skuId,isChecked,userId);
        }else{
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }
}
