package com.singpeng.service;

import com.singpeng.entity.PageResult;
import com.singpeng.entity.QueryPageBean;
import com.singpeng.pojo.CheckItem;

import java.util.List;

/**
 * 检查项服务接口
 */
public interface CheckItemService {
    public void add(CheckItem checkItem);
    //public PageResult pageQuery(Integer currentPage,Integer pageSize,String queryString);
    public PageResult pageQuery(QueryPageBean queryPageBean);

    public void delete(Integer id);

    public void edit(CheckItem checkItem);
    public CheckItem findById(Integer id);

    public List<CheckItem> findAll();
}
