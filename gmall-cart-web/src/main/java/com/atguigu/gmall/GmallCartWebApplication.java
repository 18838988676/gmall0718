package com.atguigu.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GmallCartWebApplication {
//为了登录拦截器能被扫描到  才 放到gmall一层        com.atguigu.gmall.interceptor.AuthInterceptor
    public static void main(String[] args) {
        SpringApplication.run(GmallCartWebApplication.class, args);
    }

}
