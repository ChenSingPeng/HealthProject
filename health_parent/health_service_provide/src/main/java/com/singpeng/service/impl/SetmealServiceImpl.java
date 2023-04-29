package com.singpeng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.singpeng.dao.SetmealDao;
import com.singpeng.pojo.Setmeal;
import com.singpeng.service.SetmealService;
import org.apache.poi.ss.formula.functions.Intercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 体检套餐服务实现类
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl  implements SetmealService{
    @Autowired
    private SetmealDao setmealdao;

    //新增套餐
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds){
        setmealdao.add(setmeal);
        if (checkgroupIds != null && checkgroupIds.length>0){
            // bind relation N:M
            setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        }
        //setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
    }

    // bind setmeal and checkgroup relation N:M
    private void setSetmealAndCheckGroup(Integer id, Integer[] checkgroupIds){
        for (Integer checkgroupId : checkgroupIds) {
            Map<String,Integer> map = new HashMap<>();
            map.put("setmeal_id", id);
            map.put("checkgroup_id",checkgroupId);
            System.out.println(id+"  "+checkgroupId);
            setmealdao.setSetmealAndCheckGroup(map);
        }
    }
}
