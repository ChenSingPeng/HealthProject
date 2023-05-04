package com.singpeng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.singpeng.dao.OrderSettingDao;
import com.singpeng.pojo.Order;
import com.singpeng.pojo.OrderSetting;
import com.singpeng.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    //批量添加
    @Override
    public void add(List<OrderSetting> list) {
        if (list != null && list.size()>0){
            for (OrderSetting orderSetting : list) {
                //检查日期是否存在
                long count  = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (count>0){
                    //已经存在，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else{
                    //不存在，执行更新操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }

    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {// 示例2019-3
        String dateBegin = date + "-1";//2019-3-1
        String dateEnd = date + "-31";//2019-3-31
        Map map = new HashMap<>();
        map.put("dateBegin",dateBegin);
        map.put("dateEnd",dateEnd);
        //查询月份的天数，用于初始化预约日历
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> data = new ArrayList<>();//存储查询日期区间的每日的预约数据
        for (OrderSetting orderSetting : list) {
            Map orderSettingMap = new HashMap();//存储每日的预约数据
            orderSettingMap.put("date",orderSetting.getOrderDate().getDate());//获得日期
            orderSettingMap.put("number",orderSetting.getNumber());//可预约人数
            orderSettingMap.put("reservations",orderSetting.getReservations());//已预约人数
            data.add(orderSettingMap);
        }
        return data;
    }

    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if (count>0){
            //已经存在，执行更新操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            //不存在，执行更新操作
            orderSettingDao.add(orderSetting);
        }
    }
}
