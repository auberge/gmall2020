package com.auberge.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.auberge.gmall.bean.UserAddress;
import com.auberge.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class OrderController {
    /*    @RequestMapping("trade")
        public String trade(){
            //返回一个视图名为index.html
            return "index";
        }*/
//    @Autowired
    @Reference
    private UserService userService;

    @RequestMapping("trade")
    @ResponseBody
    public List<UserAddress> trade(String userId) {
        return userService.getUserAddressList(userId);
    }
}
