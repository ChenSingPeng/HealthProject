package com.singpeng.service;

import com.singpeng.entity.PageResult;
import com.singpeng.pojo.Setmeal;

import java.util.List;

/**
 * 套餐服务接口
 */
public interface SetmealService {

    public void add(Setmeal setmeal, Integer[] checkgroupIds);

    PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString);

    public Setmeal findById(Integer id);

    public List<Integer> findCheckGroupIdsBySetmealId(Integer id);

    public void edit(Setmeal setmeal, Integer[] checkgroupIds);

    public void deleteById(Integer id);

    public List<Setmeal> findAll();


}
