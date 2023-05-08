package com.singpeng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.singpeng.constant.MessageConstant;
import com.singpeng.dao.MemberDao;
import com.singpeng.dao.OrderDao;
import com.singpeng.dao.OrderSettingDao;
import com.singpeng.entity.Result;
import com.singpeng.pojo.Member;
import com.singpeng.pojo.Order;
import com.singpeng.pojo.OrderSetting;
import com.singpeng.service.OrderService;
import com.singpeng.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    //体检预约
    @Override
    public Result order(Map map) throws Exception {
        //检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行预约
        String orderdate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderdate);
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(date);
        if (orderdate==null){
            //指定的日期没有设置开始预约
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //检查用户所选择的预约日期是否已经约满，如果已经约满则无法预约
        int number = orderSetting.getNumber();
        int reservations = orderSetting.getReservations();
        if (reservations>=number){
            //预约满l
            return new Result(false,MessageConstant.ORDERSETTING_FAIL);
        }
        //检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
        String telephone = (String) map.get("telponeOrEmail");
        Member member = memberDao.findByTelephone(telephone);
        if (member!=null){
            //判断是否为空
            Integer memberId = member.getId();
            Date order_date = DateUtils.parseString2Date(orderdate);
            String setmealId = (String) map.get("setmealId");
            System.out.println(setmealId);
            Order order = new Order(memberId,order_date,Integer.parseInt(setmealId));
            //根据条件查询
            List<Order> list = orderDao.findByCondition(order);
            if (list!=null&&list.size()>0){
                //已经预约过了，无法在进行预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }

        }else {
            //检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);//会员注册
        }



        //预约成功，更新当日的已预约人数
        /*Order order = new Order();
        order.setId(member.getId());
        System.out.println(member.getId());
        order.setOrderDate(date);
        order.setOrderStatus((String) map.get("orderType"));
        order.setSetmealId(Integer.parseInt((String) map.get("setmealId")));*/
        Order order = new Order(member.getId(),
                date,
                (String)map.get("orderType"),
                Order.ORDERSTATUS_NO,
                Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);
        orderSetting.setReservations(reservations+1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    //根据id查询体检人信息预约日期，套餐名称，预约类型
    @Override
    public Map findById(Integer id) throws Exception {
        Map map = orderDao.findById4Detail(id);
        if (map!=null){
            //处理日期格式
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }
}
