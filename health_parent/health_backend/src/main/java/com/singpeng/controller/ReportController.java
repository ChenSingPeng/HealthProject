package com.singpeng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.singpeng.constant.MessageConstant;
import com.singpeng.entity.Result;
import com.singpeng.service.MemberService;
import com.singpeng.service.SetmealService;
import com.sun.tools.classfile.ConstantPool;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    /**
     * 会员数量统计
     *
     */
    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-12);//获取当前日期12个月的日期
        List<String> list = new ArrayList<>();
        for (int i = 0;i<12;i++){
            calendar.add(Calendar.MONTH,1);
            list.add(new SimpleDateFormat("yyyy.MM").format(calendar.getTime()));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("months",list);
        List<Integer> memberCount = memberService.findMemberCountByMonth(list);
        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }


    /**
     * 套餐占比统计
     * @return
     */
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        List<Map<String, Object>> list = setmealService.findSetmealCount();
        Map<String,Object> map = new HashMap<>();
        map.put("setmealCount",list);
        List<String> setmealName = new ArrayList<>();
        for (Map<String, Object> m : list) {
            String name = (String) m.get("name");
            setmealName.add(name);
        }
        map.put("setmealName",setmealName);
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
    }
}
