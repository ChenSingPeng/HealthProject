package com.singpeng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.singpeng.constant.MessageConstant;
import com.singpeng.constant.RedisConstant;
import com.singpeng.entity.PageResult;
import com.singpeng.entity.QueryPageBean;
import com.singpeng.entity.Result;
import com.singpeng.pojo.CheckGroup;
import com.singpeng.pojo.CheckItem;
import com.singpeng.pojo.Setmeal;
import com.singpeng.service.SetmealService;
import com.singpeng.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    private SetmealService setmealService;

    @Autowired
    private JedisPool jedisPool;

    //图片上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile")MultipartFile imgFile){
        try {
            //获取文件名
            String originalFilename = imgFile.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            //获取文件后缀
            String suffix = originalFilename.substring(lastIndexOf - 1);
            //使用UUID随机生成文件名
            String fileName = UUID.randomUUID().toString() + suffix;
            QiniuUtils.upload2Qiniu(imgFile.getBytes(), fileName);
            //图片上传成功
            Result result = new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS);
            result.setData(fileName);
            //将上传图片名称存入Redis，基于Redis的Set集合存储+
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            return result;
        } catch (IOException e) {
            //上传图片失败
            e.printStackTrace();
            return new Result(false,MessageConstant.PIC_UPLOAD_FAIL);
        }
    }
    //新增
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        try {
            setmealService.add(setmeal,checkgroupIds);
            System.out.println(setmeal.toString());
            System.out.println(Arrays.toString(checkgroupIds));
        }catch (Exception e){
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = setmealService.pageQuery(
                queryPageBean.getCurrentPage(),
                queryPageBean.getPageSize(),
                queryPageBean.getQueryString());
        return pageResult;
    }

    //根据id查询
    @RequestMapping("/findByIdToEdit")
    public Result findById(Integer id){
        try {
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        } catch (Exception e){
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    //根据检查组合id查询对应的所有检查项id
    @RequestMapping("/findCheckGroupIdsBySetmealId")
    public Result findCheckGroupIdsBySetmealId(Integer id){
        try {
            List<Integer> checkgroupIds = setmealService.findCheckGroupIdsBySetmealId(id);
            return new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkgroupIds);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }
    //edit
    @RequestMapping("/edit")
    public Result edit(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        try {
            setmealService.edit(setmeal,checkgroupIds);
        }catch (Exception e){
            return new Result(false,MessageConstant.EDIT_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.EDIT_SETMEAL_SUCCESS);
    }
    //删除
    @RequestMapping("/delete")
    public Result deleteById(Integer id){
        try {
            setmealService.deleteById(id);
        }catch (Exception e){
            return new Result(false,MessageConstant.DELETE_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.DELETE_SETMEAL_SUCCESS);
    }
}
