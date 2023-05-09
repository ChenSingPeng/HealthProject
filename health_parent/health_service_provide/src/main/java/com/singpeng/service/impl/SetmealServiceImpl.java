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
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.poi.ss.formula.functions.Intercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
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

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${out_put_path}")
    private String outPutPath;

    //新增套餐
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds){
        setmealdao.add(setmeal);
        Integer setmealId = setmeal.getId();
        if (checkgroupIds != null && checkgroupIds.length>0){
            // bind relation N:M
            setSetmealAndCheckGroup(setmeal.getId(),checkgroupIds);
        }
        //将图片的名称保存至Redis
        savePicToRedis(setmeal.getImg());
        //新增套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }
    //生成静态页面
    private void generateMobileStaticHtml() {
        //准备模板文件
        List<Setmeal> setmealList = setmealdao.findAll();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情页面
        generateMobileSetmealDetailHtml(setmealList);
    }

    private void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal : setmealList) {
            Map<String, Object> dataMap = new HashMap<String,Object>();
            dataMap.put("setmeal",setmealdao.findByIdToDetail(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl","setmeal_detail_"+setmeal.getId()+".html",dataMap);
        }
    }

    //生成套餐列表静态页面
    private void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String,Object> dataMap =new HashMap<String,Object>();
        dataMap.put("setmealList",setmealList);
        this.generateHtml("mobile_setmeal.ftl","m_setmeal.html",dataMap);
    }

    private void generateHtml(String templateName,String htmlPageName, Map<String, Object> dataMap) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer out = null;
        try{
            //加载模板
            Template template = configuration.getTemplate(templateName);
            //生成数据
            File docFile = new File(outPutPath + "\\" + htmlPageName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            //输出文件
            template.process(dataMap,out);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (null!=out){
                    out.flush();
                }
            }catch (Exception e2){
                e2.printStackTrace();
            }
        }
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

    @Override
    public List<Setmeal> findAll() {
        return setmealdao.findAll();
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealdao.findSetmealCount();
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
