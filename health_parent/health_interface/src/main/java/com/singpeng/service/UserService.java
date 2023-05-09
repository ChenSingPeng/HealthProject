package com.singpeng.service;

import com.singpeng.pojo.User;

/**
 * 用户服务接口
 */
public interface UserService {
    public User findByUserName(String userName);
}
