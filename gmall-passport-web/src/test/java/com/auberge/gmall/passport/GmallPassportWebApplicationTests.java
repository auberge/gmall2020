package com.auberge.gmall.passport;

import com.auberge.gmall.passport.utils.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPassportWebApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testJWT(){
        String key="auberge";
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",1001);
        map.put("nickName","admin");
        String salt="192.168.88.129";
        String token = JwtUtil.encode(key, map, salt);
        System.out.println("token:"+token);
        Map<String, Object> maps = JwtUtil.decode(token, key, salt);
        System.out.println("map="+maps);
    }
}
