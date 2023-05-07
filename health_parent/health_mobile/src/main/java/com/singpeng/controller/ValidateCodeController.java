package com.singpeng.controller;

import com.aliyuncs.exceptions.ClientException;
import com.singpeng.constant.EmailMessgeConstant;
import com.singpeng.constant.MessageConstant;
import com.singpeng.constant.RedisMessageConstant;
import com.singpeng.entity.Result;
import com.singpeng.utils.MailUtils;
import com.singpeng.utils.SMSUtils;
import com.singpeng.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * 发送验证码
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;

    //体检预约验证码
    @RequestMapping("/sendToOrder")
    public Result sendToOrder(String telponeOrEmail,String inputType){
        Integer code = ValidateCodeUtils.generateValidateCode(4);//生成4位验证码
        if ("email".equals(inputType)){
            //发邮件
            try {
                MailUtils.sendMail(telponeOrEmail, EmailMessgeConstant.EMAIL_CODE_MSG1+code.toString()+
                        EmailMessgeConstant.EMAIL_CODE_MSG2,EmailMessgeConstant.EMAIL_TITLE);
            }catch (Exception e){
                e.printStackTrace();
                return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
            }
        }else {
            //发短信
            try {
                SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telponeOrEmail,code.toString());
            }catch (ClientException e){
                e.printStackTrace();
                return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
            }
        }
        System.out.println("已发送验证码: "+code+"==========>"+telponeOrEmail);
        //将验证码保存Redis缓存
        jedisPool.getResource().setex(
                telponeOrEmail+ RedisMessageConstant.SENDTYPE_ORDER,5*60,code.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
