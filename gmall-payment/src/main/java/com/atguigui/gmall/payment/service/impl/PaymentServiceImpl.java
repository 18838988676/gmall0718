package com.atguigui.gmall.payment.service.impl;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigui.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigui.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Override
    public void savePayment(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {

        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",paymentInfo.getOutTradeNo());

        paymentInfoMapper.updateByExampleSelective(paymentInfo,example);

    }
}
