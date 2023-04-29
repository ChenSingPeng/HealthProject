package com.singpeng.service;

import com.singpeng.pojo.Setmeal;

/**
 * 套餐服务接口
 */
public interface SetmealService {

    public void add(Setmeal setmeal, Integer[] checkgroupIds);
}
