package com.auberge.gmall.service;

import com.auberge.gmall.bean.UserAddress;
import com.auberge.gmall.bean.UserInfo;

import java.util.List;

public interface UserService {
    /**
     * 查询所有数据
     */
    List<UserInfo> findAll();

    /**
     * 根据用户id查询用户地址列表
     * @param userId
     * @return
     */
    List<UserAddress> getUserAddressList(String userId);

    /**
     * 用户登录
     * @param userInfo
     * @return
     */
    UserInfo login(UserInfo userInfo);

    /**
     * 根据用户
     * @param userId
     * @return
     */
    UserInfo verify(String userId);
}
