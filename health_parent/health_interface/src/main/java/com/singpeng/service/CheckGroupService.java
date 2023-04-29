package com.singpeng.service;

import com.singpeng.entity.PageResult;
import com.singpeng.pojo.CheckGroup;

import java.util.List;

/**
 * 检查组服务接口
 */
public interface CheckGroupService {
    public void add(CheckGroup checkGroup, Integer[] checkItemIds);
    public PageResult pageQuery(Integer currentPage,Integer pageSize,String queryString);

    public CheckGroup findById(Integer id);

    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    public void edit(CheckGroup checkGroup, Integer[] checkitemIds);

    public void deleteById(Integer id);

    public List<CheckGroup> findAll();
}
