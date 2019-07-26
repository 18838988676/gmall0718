package com.atguigu.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall.user.mapper")
public class GmallUserApplication {

    public static void main(String[] args) {
//因为要使用redis   包的关系 。。。。  还要配置redis的连接配置
        System.out.println("cesdf");
        SpringApplication.run(GmallUserApplication.class, args);
    }

}
