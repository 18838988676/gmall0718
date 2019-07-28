package com.atguigui.gmall.payment.mq;


import com.atguigui.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Map;

//巡检监听  给支付宝发送支付申请后 监听支付宝扣费的状态  不让用户久等之类的
//        PayController.apipy方法：在申请支付宝后  设置延迟队列
//        @RequestMapping("alipay/submit")
//         public String alipay
//          System.out.println("设置一个定时巡检订单"+paymentInfo.getOutTradeNo()+"的支付状态的延迟队列");
//           paymentService.sendPaymentCheckQueue(paymentInfo.getOutTradeNo(),5);
//
@Component
public class PaymentCheckListener {

    @Autowired
    PaymentService paymentService;

    @JmsListener(containerFactory = "jmsQueueListener", destination = "PAYMENT_CHECK_QUEUE")
    public void consumePaymentSuccess(MapMessage mapMessage) throws JMSException {

        int count = mapMessage.getInt("count");
        String outTradeNo = mapMessage.getString("outTradeNo");


        // 检查支付状态   //尽量别在监听器中写业务逻辑  要写的话 就调用
        Map<String, String> stringStringMap = paymentService.checkPayment(outTradeNo);

        String status = stringStringMap.get("status");
        String callbackContent = stringStringMap.get("callbackContent");
        String tradeNo = stringStringMap.get("tradeNo");
//status=trade_status:  https://docs.open.alipay.com/api_1/alipay.trade.query
// 支付宝回调参数：TRADE_SUCCESS  交易支付成功、TRADE_FINISHED 交易结束，不可退款 、 TRADE_CLOSED 未付款交易超时关闭，或支付完成后全额退款
        if ( "TRADE_SUCCESS".equals(status) || "TRADE_CLOSED".equals(status)) {

            // 幂等性检查
            boolean b = paymentService.checkPaied(outTradeNo);
            // 发送支付成功的队列
            if (!b) {
                //发送支付成功的队列
                paymentService.sendPaymentSuccessQueue(tradeNo, outTradeNo, callbackContent);
            }

        } else {
            if (count > 0) {
                System.out.println("监听到延迟检查队列，执行延迟检查第" + (6 - count) + "次检查");
                paymentService.sendPaymentCheckQueue(outTradeNo, (count - 1));
            } else {
                System.out.println("监听到延迟检查队列次数耗尽，结束检查");
            }
        }


    }
}
