package com.singpeng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.singpeng.constant.MessageConstant;
import com.singpeng.entity.Result;
import com.singpeng.pojo.Setmeal;
import com.singpeng.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    private SetmealService setmealService;

    //查询所有
    @RequestMapping("/getAllSetmeal")
    public Result getAllSetmeal(){
        try {
            List<Setmeal> list = setmealService.findAll();
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS,list);
        }catch (Exception e){
            return new Result(false,MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }
    //根据套餐查询数据
    @RequestMapping("/findById")
    public Result findById(int id){
        try {
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS,setmeal);
        }catch (Exception e){
            return new Result(false,MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }
}
