package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map){

        return "item";
    }


    @RequestMapping("index")
    public String index(ModelMap map){

        map.put("hello","hello thymeleaf");

        List<UserInfo> userInfos = new ArrayList<UserInfo>();
        for (int i = 0; i <5 ; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setNickName("小"+i);
            userInfo.setPhoneNum("12333333333");

            userInfos.add(userInfo);
        }

        map.put("userInfos",userInfos);
        return "demo";
    }
}
