package com.auberge.gmall.cart.service.Impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.auberge.gmall.bean.CartInfo;
import com.auberge.gmall.bean.SkuInfo;
import com.auberge.gmall.cart.constant.CartConst;
import com.auberge.gmall.cart.mapper.CartInfoMapper;
import com.auberge.gmall.config.RedisUtil;
import com.auberge.gmall.service.CartService;
import com.auberge.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartInfoMapper cartInfoMapper;
    @Reference
    private ManageService manageService;
    @Autowired
    private RedisUtil redisUtil;

    //添加的是登录的还是没有登录的，在控制器判断
    //登录时添加购物车
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        /*
        1. 先查询购物车中是否有相同的商品，如果购物车中有相同商品，则数量加1
        2. 如果没有，直接添加到redis和MySQL数据库
        3. 更新缓存
        */
        //先通过skuId和userId查询是否有该商品
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
        //有相同的商品
        if (cartInfoExist != null) {
            //数量相加
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum);
            //给skuPrice初始化操作 skuPrice=cartPrice
            cartInfoExist.setSkuPrice(cartInfoExist.getCartPrice());
            //更新数据
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
        } else {
            //没有相同的商品
            //cartInfo数据来源域尚帕尼详情页，也就是来源于skuInfo
            //根据skuId查询skuInfo
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            //属性赋值
            cartInfo.setSkuId(skuInfo.getId());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCartPrice(skuInfo.getPrice());
            //添加到数据库
            cartInfoMapper.insertSelective(cartInfo);
            cartInfoExist = cartInfo;
        }
        Jedis jedis = redisUtil.getJedis();
        //定义购物车的key
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        //采用那种数据类型存储 Hash
        jedis.hset(cartKey, skuId, JSON.toJSONString(cartInfoExist));
        jedis.close();
    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        //定义购物车的key
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        List<String> list = jedis.hvals(cartKey);
        if (list != null && list.size() > 0) {
            for (String cartInfoStr : list) {
                cartInfoList.add(JSON.parseObject(cartInfoStr, CartInfo.class));
            }
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    //定义比较规则
                    //compareTo 比较规则：s1=abc s2=abcd
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        } else {
            cartInfoList = loadCartCache(userId);
            return cartInfoList;
        }
    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId) {
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        //开始合并
        for (CartInfo cartInfoCK : cartListCK) {
            //定义一个 boolean 类型变量
            boolean isMatch = false;
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if (cartInfoDB.getSkuId().equals(cartInfoCK.getSkuId())) {
                    //将数量进行相加
                    cartInfoDB.setSkuNum(cartInfoCK.getSkuNum() + cartInfoDB.getSkuNum());
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                    isMatch = true;
                }
            }
            if (!isMatch) {
                //未登录的对象添加到数据库
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }
        return loadCartCache(userId);
    }

    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        //获取redis客户端并获取购物车信息，直接修改skuId商品的勾选状态
        Jedis jedis = redisUtil.getJedis();
        //定义购物车的key
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        String cartInfoJson = jedis.hget(cartKey, skuId);
        CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
        cartInfo.setIsChecked(isChecked);
        jedis.hset(cartKey,userId,JSON.toJSONString(cartInfo));
        //为方便结算，新建一个购物车
        String cartKeyChecked=CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CHECKED_KEY_SUFFIX;
        if ("1".equals(isChecked)){
            jedis.hset(cartKeyChecked,skuId,JSON.toJSONString(cartInfo));
        }else{
            jedis.hdel(cartKeyChecked,skuId);
        }
        jedis.close();
    }

    //从购物车中查询实时价格
    private List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList == null || cartInfoList.size() == 0) {
            return null;
        }
        Jedis jedis = redisUtil.getJedis();
        //定义购物车的key
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        //cartInfoList 从数据库中查到的数据放入redis
        /*for (CartInfo cartInfo : cartInfoList) {
            jedis.hset(cartKey,cartInfo.getSkuId(),JSON.toJSONString(cartInfo));
        }*/
        HashMap<String, String> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
        }
        //一次放入多天数据
        jedis.hmset(cartKey, map);
        jedis.close();
        return cartInfoList;
    }
}
