package com.auberge.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//@EnableDubbo(scanBasePackages = "com.auberge.gmall")
@MapperScan(basePackages = "com.auberge.gmall.user.mapper")
@ComponentScan(basePackages = "com.auberge.gmall")
public class GmallUserManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUserManageApplication.class, args);
    }

}
