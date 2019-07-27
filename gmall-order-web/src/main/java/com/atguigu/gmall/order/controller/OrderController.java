package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotation.LoginRequire;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {



    @Reference
    CartService cartService;

    @Reference
    UserService userService;

    @Reference
    OrderService orderService;

    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("submitOrder")
    private String submitOrder(String tradeCode,HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        String userId = (String) request.getAttribute("userId");
        // 比较交易码
        boolean b = orderService.checkTradeCode(tradeCode,userId);

        if(b){
            // 执行提交订单的业务
        }else{
            map.put("errMsg","获取订单信息失败");
            return "tradeFail";
        }



        return "payTest";
    }

    /***
     * 订单系统中，必须登陆才能访问的方法
     * @param request
     * @param response
     * @param cartInfo
     * @param map
     * @return
     */
    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("toTrade")
    private String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        // 需要被单点登录的拦截器
        String userId = (String) request.getAttribute("userId");

        // 将被选中的购物车对象转化为订单对象，展示出来
        List<CartInfo> cartInfos = cartService.getCartCacheByChecked(userId);
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartInfo cartInfo : cartInfos) {
            OrderDetail orderDetail = new OrderDetail();
            // 将购物车对象转化为订单对象
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());

            orderDetails.add(orderDetail);
        }

        // 查询用户收获地址列表，让用户选择
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);

        // 生成交易码
        String tradeCode = orderService.genTradeCode(userId);
        map.put("tradeCode", tradeCode);
        map.put("userAddressList", userAddressList);
        map.put("orderDetailList", orderDetails);
        map.put("totalAmount", getTotalPrice(cartInfos));
        return "trade";
    }


    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {
        BigDecimal b = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {

            if(cartInfo.getIsChecked().equals("1")){
                b = b.add(cartInfo.getCartPrice());
            }

        }
        return b;
    }



}
