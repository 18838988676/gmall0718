package com.atguigu.gmall.passport.controller;

import com.atguigu.gmall.bean.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PassportController {

    /***
     * 登陆页
     * @return
     */
    @RequestMapping("index")
    public String index(String returnURL, ModelMap map){

        map.put("returnURL",returnURL);
        return "index";
    }

    /***
     * 颁发token
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo){

        // 调用用户服务验证用户名和密码

        // 颁发token

        // 重定向原始业务

        return "token";
    }

    /***
     * 验证token
     * @return
     */
    @ResponseBody
    @RequestMapping("verify")
    public String verify(){
        return "success";
    }



}
