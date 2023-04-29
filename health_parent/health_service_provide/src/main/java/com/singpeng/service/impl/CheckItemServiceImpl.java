package com.singpeng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.singpeng.dao.CheckItemDao;
import com.singpeng.entity.PageResult;
import com.singpeng.entity.QueryPageBean;
import com.singpeng.pojo.CheckItem;
import com.singpeng.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 检查项服务
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService{
    @Autowired
    private CheckItemDao checkItemDao;
    //add
    public void add(CheckItem checkItem){
        checkItemDao.add(checkItem);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
/*        if (page.getTotal()<pageSize){
            PageHelper.startPage(1,pageSize);
        }*/
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public void delete(Integer id) throws RuntimeException {
        //检查当前检查是否和检查组关联
        long count = checkItemDao.findCountByCheckItemId(id);
        if (count >0){
            //当前检查项被应用，不能删除
            throw new RuntimeException("当前检查项被应用，不能删除");

        }
        checkItemDao.deleteById(id);

    }

    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}
