package com.singpeng.jobs;

import com.singpeng.constant.RedisConstant;
import com.singpeng.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class ClearImgJob {
    @Autowired
    private JedisPool jedisPool;
    //定时清理图片
    public void clearImg(){
        //计算Redis中的两个集合差值，获得垃圾图片集合
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if (set!=null){
            for (String picName : set) {
                //删除云端仓库的垃圾图片
                QiniuUtils.deleteFileFromQiniu(picName);
                //删除Redisd中的已删除的图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);
                System.out.println("Clear img: "+picName);
            }


        }
    }
}
