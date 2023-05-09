package com.singpeng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.singpeng.dao.PermissionDao;
import com.singpeng.dao.RoleDao;
import com.singpeng.dao.UserDao;
import com.singpeng.pojo.Permission;
import com.singpeng.pojo.Role;
import com.singpeng.pojo.User;
import com.singpeng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;
    @Override
    public User findByUserName(String username) {
        User user = userDao.findByUsername(username);
        if (user==null){
            return null;
        }

        Integer userId = user.getId();
        Set<Role> roles = roleDao.findByUserId(userId);
        if (roles !=null && roles.size() >0){
            for (Role role : roles) {
                Integer roleId = role.getId();
                Set<Permission> permissions = permissionDao.findByRoleId(roleId);
                if (permissions !=null && permissions.size()>0){
                    role.setPermissions(permissions);
                }
            }
            user.setRoles(roles);
        }
        return user;
    }
}
