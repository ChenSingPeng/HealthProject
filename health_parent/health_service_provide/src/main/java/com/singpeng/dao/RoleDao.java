package com.singpeng.dao;

import com.singpeng.pojo.Role;

import java.util.Set;

public interface RoleDao {
    Set<Role> findByUserId(Integer userId);
}
