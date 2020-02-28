package com.auberge.gmall.user.serivce.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.auberge.gmall.bean.UserAddress;
import com.auberge.gmall.bean.UserInfo;
import com.auberge.gmall.config.RedisUtil;
import com.auberge.gmall.service.UserService;
import com.auberge.gmall.user.mapper.UserAddressMapper;
import com.auberge.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;


import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Resource
    private RedisUtil redisUtil;

    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        //调用mapper
        return userAddressMapper.select(userAddress);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        //使用md5对密码进行加密
        String passwd = userInfo.getPasswd();
        String newPwd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(newPwd);
        UserInfo user = userInfoMapper.selectOne(userInfo);
        if (user!=null){
            //设置放入redis的key
            String userKey= userKey_prefix+user.getId()+userinfoKey_suffix;
            //获取jedis
            Jedis jedis = redisUtil.getJedis();
            //放入jedis使用的数据类型Stirng
            jedis.setex(userKey,userKey_timeOut, JSON.toJSONString(user));
            //关闭jedis
            jedis.close();
        }
        return user;
    }

    @Override
    public UserInfo verify(String userId) {
        Jedis jedis = redisUtil.getJedis();
        try {
            String userKey = userKey_prefix + userId + userinfoKey_suffix;
            String userJson = jedis.get(userKey);
            if (!StringUtils.isEmpty(userJson)) {
                UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
                return userInfo;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
}
