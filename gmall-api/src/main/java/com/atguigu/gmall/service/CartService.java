package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {
    CartInfo ifCartExists(CartInfo cartInfo);

    void updateCart(CartInfo cartInfoDb);

    void saveCart(CartInfo cartInfo);

    void syncCache(String userId);

    List<CartInfo> getCartCache(String userId);

    void updateCartChecked(CartInfo cartInfo);
}
