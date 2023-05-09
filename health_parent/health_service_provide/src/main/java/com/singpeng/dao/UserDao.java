package com.singpeng.dao;

import com.singpeng.pojo.User;

public interface UserDao {
    User findByUsername(String username);
}
