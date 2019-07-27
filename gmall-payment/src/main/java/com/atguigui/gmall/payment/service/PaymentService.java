package com.atguigui.gmall.payment.service;

import com.atguigu.gmall.bean.PaymentInfo;

public interface PaymentService {
    void updatePayment(PaymentInfo paymentInfo);

    void savePayment(PaymentInfo paymentInfo);
}
