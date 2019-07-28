package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.config.ActiveMQUtil;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public String genTradeCode(String userId) {

        String k = "user:"+userId+":tradeCode";
        String v = UUID.randomUUID().toString();

        Jedis jedis = redisUtil.getJedis();
        jedis.setex(k,60*30,v);

        jedis.close();

        return v;
    }

    @Override
    public String saveOrder(OrderInfo orderInfo) {
        orderInfoMapper.insertSelective(orderInfo);
        String orderId = orderInfo.getId();

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insertSelective(orderDetail);
        }

        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrderById(String orderId) {
        OrderInfo orderInfo1 = new OrderInfo();
        orderInfo1.setId(orderId);
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderInfo1);


        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> select = orderDetailMapper.select(orderDetail);

        orderInfo.setOrderDetailList(select);
        return orderInfo;
    }

    @Override
    public void updateOrderStatus(OrderInfo orderInfo) {
        Example example = new Example(OrderInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",orderInfo.getOutTradeNo());
        orderInfoMapper.updateByExampleSelective(orderInfo,example);
    }

    @Override
    public void sendOrderResultQueue(String outTradeNo) {
        try {
            //  建立mq的连接
            Connection connection = activeMQUtil.getConnection();
            connection.start();

            // 通过连接创建一次于mq的回话任务
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            Queue testqueue = session.createQueue("ORDER_RESULT_QUEUE");

            // 通过mq的回话任务将队列消息发送出去
            MessageProducer producer = session.createProducer(testqueue);

            TextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText(outTradeNo);
            // 延迟队列 并不在这里设置  textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*60);


            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);

            // 提交任务
            session.commit();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

        System.out.println("订单支付成功，发送订单的消息队列。。");
    }

    @Override
    public boolean checkTradeCode(String tradeCode, String userId) {
        String k = "user:"+userId+":tradeCode";
        boolean b = false;

        Jedis jedis = redisUtil.getJedis();
        String s = jedis.get(k);
        if(StringUtils.isNotBlank(s)&&s.equals(tradeCode)){
            b = true;
            jedis.del(k);
        }

        return b;
    }
}
