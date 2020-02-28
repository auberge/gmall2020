package com.auberge.gmall.config;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AutoInterceptor extends HandlerInterceptorAdapter {
    //多个拦截器执行的顺序
    //跟配置文件中，配置拦截器的顺序有关系，先进后出

    //用户进入控制器之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("newToken");
        //当token不为null时，放入cookie
        if (token != null) {
            CookieUtil.setCookie(request, response, "token", token, WebConst.COOKIE_MAXAGE, false);
        }
        //当用户访问非登录之后的页面，继续访问其他业务模块时，url并没有newToken，但是后台可能将token放入了cookie
        if (token == null) {
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        if (token != null) {
            //开始接密token获取nickName
            Map map = getUserMapByToken(token);
            String nickName = (String) map.get("nickName");
            request.setAttribute("nickName", nickName);
        }

        // 在拦截器中获取方法上的注解！
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取方法上的注解LoginRequire
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (methodAnnotation != null) {
            //此时有注解
            //判断是否已经登陆
            //获取服务器上的salt
            String salt = request.getHeader("X-forwarded-for");
            //调用verify（）认证
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&salt=" + salt);
            if ("success".equals(result)) {
                //登录成功 保存userId 解密token获取nickName
                Map map = getUserMapByToken(token);
                //取出userId
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
                return true;
            } else {
                //认证失败，并且                methodAnnotation.autoRedirect()=true:必须登录
                if (methodAnnotation.autoRedirect()) {
                    //跳转到页面
                    String requestURL = request.getRequestURL().toString();
                    //将url进行转码
                    String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
                    response.sendRedirect(WebConst.LOGIN_ADDRESS + "?originUrl=" + encodeURL);
                    return false;
                }
            }
        }
        return true;
    }

    //解密token获取map数据
    private Map getUserMapByToken(String token) {
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        //将tokenUserInfo进行base64解密
        byte[] decode = base64UrlCodec.decode(tokenUserInfo);
        String mapJson = null;
        try {
            mapJson = new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(mapJson, Map.class);
    }

    //进入控制器之后，视图渲染之前
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    //视图渲染之后
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
