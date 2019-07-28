package com.atguigui.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.service.OrderService;
import com.atguigui.gmall.payment.config.AlipayConfig;
import com.atguigui.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PayController {

    @Reference
    OrderService orderService;

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;



    @RequestMapping(value = "alipay/callback/return")
    public String callbackReturn(HttpServletRequest request, String orderId, ModelMap map){

        Map<String, String> paramsMap = null; //将异步通知中收到的所有参数都存放到map中
        boolean signVerified = true;
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        } catch (Exception e) {
            System.out.println("此处支付宝的签名验证通过。。。");
        }

        if(signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            String tradeNo = request.getParameter("trade_no");
            String outTradeNo = request.getParameter("out_trade_no");
            String tradeStatus = request.getParameter("trade_status");

            String callbackContent =request.getQueryString();


            // 幂等性检擦
            boolean b = paymentService.checkPaied(outTradeNo);
            // 发送支付成功的消息PAYMENT_SUCCESS_QUEUE
            if(!b){
                paymentService.sendPaymentSuccessQueue(tradeNo,outTradeNo,callbackContent);
            }

        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            // 返回失败页面
        }


        return "testPaySuccess";
    }

    //延迟队列的起点
    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipay(String orderId, ModelMap map){
        String userId = "2";

        OrderInfo order = orderService.getOrderById(orderId);

        // 生成和保存支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderId(orderId);
        paymentInfo.setTotalAmount(order.getTotalAmount());
        paymentInfo.setSubject(order.getOrderDetailList().get(0).getSkuName());
        paymentInfo.setCreateTime(new Date());
        paymentService.savePayment(paymentInfo);

        // 重定向到支付宝平台
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("out_trade_no",order.getOutTradeNo());
        stringObjectHashMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        stringObjectHashMap.put("total_amount",0.01);//orderById.getTotalAmount()
        stringObjectHashMap.put("subject","王明超之支付宝接口测试");

        String json = JSON.toJSONString(stringObjectHashMap);
        alipayRequest.setBizContent(json);

        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //打印出私钥       保证
        System.out.println(form);
        System.out.println("设置一个定时巡检订单"+paymentInfo.getOutTradeNo()+"的支付状态的延迟队列");
     //设置延迟队列
       paymentService.sendPaymentCheckQueue(paymentInfo.getOutTradeNo(),5);
        return form;
    }

    @RequestMapping("mx/submit")
    public String mx(String orderId, ModelMap map){
        // 重定向到财付通平台
        return "index";
    }
//localhost:8087/index?orderId=
    @RequestMapping("index")
    public String index(String orderId, ModelMap map){
        String userId="2";
        OrderInfo orderInfo = orderService.getOrderById(orderId);

        map.put("orderId",orderId);

        map.put("outTradeNo",orderInfo.getOutTradeNo());
        map.put("totalAmount",orderInfo.getTotalAmount());
        return "index";
    }
}
