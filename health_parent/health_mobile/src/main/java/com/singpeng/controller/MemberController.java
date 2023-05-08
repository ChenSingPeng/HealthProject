package com.singpeng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.singpeng.constant.MessageConstant;
import com.singpeng.constant.RedisMessageConstant;
import com.singpeng.entity.Result;
import com.singpeng.pojo.Member;
import com.singpeng.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Reference
    private MemberService memberService;
    @Autowired
    private JedisPool jedisPool;

    //手机号快速登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map){
        String teelphone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        //从redis中获取验证码
        String codeInRedis = jedisPool.getResource().get(teelphone+ RedisMessageConstant.SENDTYPE_LOGIN);
        if (codeInRedis== null || !codeInRedis.equals(validateCode)){
            //验证码错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }else {
            //判断是否为会员
            Member member = memberService.findByTelephone(teelphone);
            if (member==null){
                member = new Member();
                member.setPhoneNumber(teelphone);
                member.setRegTime(new Date());
                memberService.add(member);
            }
            //登录成功
            //写入Cookie
            Cookie cookie = new Cookie("login_member_telephone",teelphone);
            cookie.setPath("/");
            cookie.setMaxAge(60*60*24*30);//有效期30天
            response.addCookie(cookie);
            //保存会员信息到Redis
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(teelphone,60*30,json);
            return new Result(true,MessageConstant.LOGIN_SUCCESS);

        }

    }
}
