package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;

public interface OrderService {
    boolean checkTradeCode(String tradeCode, String userId);

    String genTradeCode(String userId);

    String saveOrder(OrderInfo orderInfo);

    OrderInfo getOrderById(String orderId);

    void updateOrderStatus(OrderInfo orderInfo);

    void sendOrderResultQueue(String outTradeNo);
}
