package com.singpeng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.singpeng.constant.RedisConstant;
import com.singpeng.dao.SetmealDao;
import com.singpeng.entity.PageResult;
import com.singpeng.pojo.CheckGroup;
import com.singpeng.pojo.Setmeal;
import com.singpeng.service.SetmealService;
import org.apache.poi.ss.formula.functions.Intercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private JedisPool jedisPool;

    //新增套餐
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds){
        setmealdao.add(setmeal);
        if (checkgroupIds != null && checkgroupIds.length>0){
            // bind relation N:M
            setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        }
        //将图片的名称保存至Redis
        savePicToRedis(setmeal.getImg());
        //setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
    }

    @Override
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page=setmealdao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealdao.findById(id);
    }

    @Override
    public List<Integer> findCheckGroupIdsBySetmealId(Integer id) {
        return setmealdao.findCheckGroupIdsBySetmealId(id);
    }

    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        //删除原有的关联项目
        setmealdao.deleteAssociation(setmeal.getId());
        //向中间表(t_checkgroup_checkitem)插入数据（建立检查组和检查项关联关系）
        setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        //跟新检查组
        setmealdao.edit(setmeal);
    }

    @Override
    public void deleteById(Integer id) {
        setmealdao.deleteAssociation(id);
        setmealdao.deleteById(id);

    }

    // bind setmeal and checkgroup relation N:M
    private void setSetmealAndCheckGroup(Integer setmealId, Integer[] checkgroupIds){
        for (Integer checkgroupId : checkgroupIds) {
            Map<String,Integer> map = new HashMap<>();
            map.put("setmeal_id", setmealId);
            map.put("checkgroup_id",checkgroupId);
            setmealdao.setSetmealAndCheckGroup(map);
        }
    }
    private void savePicToRedis(String pic){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
    }
}
