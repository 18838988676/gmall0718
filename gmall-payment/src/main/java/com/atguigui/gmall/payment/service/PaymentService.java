package com.atguigui.gmall.payment.service;

import com.atguigu.gmall.bean.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void updatePayment(PaymentInfo paymentInfo);

    void savePayment(PaymentInfo paymentInfo);

    boolean checkPaied(String outTradeNo);

    void sendPaymentSuccessQueue(String tradeNo, String outTradeNo, String callbackContent);

    void sendPaymentCheckQueue(String outTradeNo, int i);

    Map<String, String> checkPayment(String outTradeNo);
}
