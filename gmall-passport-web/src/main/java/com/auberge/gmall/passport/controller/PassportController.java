package com.auberge.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.auberge.gmall.bean.UserInfo;
import com.auberge.gmall.config.CookieUtil;
import com.auberge.gmall.config.WebConst;
import com.auberge.gmall.passport.utils.JwtUtil;
import com.auberge.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
    @Value("${token.key}")
    String signKey;
    @Reference
    private UserService userService;

    @RequestMapping("index.html")
    public String index(HttpServletRequest request) {
        // 获取originUrl
        String originUrl = request.getParameter("originUrl");
        System.out.println(originUrl);
        // 保存originUrl
        request.setAttribute("originUrl", originUrl);
        return "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response) {
        String salt = request.getHeader("X-forwarded-for");
        UserInfo user = userService.login(userInfo);
        if (user != null) {
            //如果登录成功之后，返回token
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("nickName", user.getNickName());
            return JwtUtil.encode(signKey, map, salt);
        } else {
            return "fail";
        }
    }

    //http://passport.gmall.com/verify?token=xxx&salt=x
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request) {
        String salt = request.getParameter("salt");
        String token = request.getParameter("token");
        Map<String, Object> map = JwtUtil.decode(token, signKey, salt);
        if (map != null && map.size() > 0) {
            String userId = (String) map.get("userId");
            UserInfo userInfo = userService.verify(userId);
            if (userInfo != null) {
                return "success";
            }
        }
        return "fail";
    }
}
