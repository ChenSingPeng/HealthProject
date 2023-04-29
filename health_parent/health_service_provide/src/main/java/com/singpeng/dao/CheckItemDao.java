package com.singpeng.dao;

import com.github.pagehelper.Page;
import com.singpeng.pojo.CheckItem;

import java.util.List;

/**
 * 持久层接口
 */
public interface CheckItemDao {
    public void add(CheckItem checkItem);

    public Page<CheckItem> selectByCondition(String queryString);

    public void deleteById(Integer id);
    public long findCountByCheckItemId(Integer checkItemId);

    void edit(CheckItem checkItem);

    CheckItem findById(Integer id);

    public List<CheckItem> findAll();

}
