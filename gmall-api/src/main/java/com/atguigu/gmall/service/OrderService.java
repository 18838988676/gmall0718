package com.atguigu.gmall.service;

public interface OrderService {
    boolean checkTradeCode(String tradeCode, String userId);

    String genTradeCode(String userId);
}
