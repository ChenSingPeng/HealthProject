package com.singpeng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.singpeng.constant.EmailMessgeConstant;
import com.singpeng.constant.MessageConstant;
import com.singpeng.constant.RedisConstant;
import com.singpeng.constant.RedisMessageConstant;
import com.singpeng.entity.Result;
import com.singpeng.pojo.Order;
import com.singpeng.service.OrderService;
import com.singpeng.utils.MailUtils;
import com.singpeng.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * 体检预约
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 体检预约
     *
     * @param map
     * @return
     */
    @RequestMapping("/submit")
    /*public Result submitOrder(@RequestParam String inputType, @RequestBody Map map) {*/
    public Result submitOrder(@RequestBody Map map) {
        String telephoneOrEmail = (String) map.get("telponeOrEmail");
        String name = (String) map.get("name");
        //从redis中查询验证码
        String codeInRedis = jedisPool.getResource().get(telephoneOrEmail + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String) map.get("validateCode");
        //校验验证码
        if (codeInRedis == null || !codeInRedis.equals(validateCode)) {
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        Result result = null;
        //调用体检预约
        try {
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            result = orderService.order(map);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        if (result.isFlag()) {
            //成功
            Thread t = new Thread(() -> {
                // 线程执行的代码逻辑
                String orderDate = (String) map.get("orderDate");
                /*if ("email".equals(inputType)) {*/
                if (telephoneOrEmail.contains("@")) {
                    try {
                        MailUtils.sendMail(telephoneOrEmail, (String) map.get("name") + EmailMessgeConstant.EMAIL_ORDER_SUCCESS1
                                + orderDate + EmailMessgeConstant.EMAIL_ORDER_SUCCESS2+EmailMessgeConstant.EMAIL_ZYSX, EmailMessgeConstant.EMAIL_TITLE);
                        System.out.println("Send order email success!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE, telephoneOrEmail, "6181");
                        System.out.println("Send order message success!");
                    } catch (ClientException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start(); // 启动线程
        }
        return result;

    }

    @RequestMapping("/findById")
    public Result finById(Integer id){
        try {
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
