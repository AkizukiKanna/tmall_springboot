package com.how2java.tmall.service;

import com.how2java.tmall.dao.PermissionDAO;
import com.how2java.tmall.pojo.Permission;
import com.how2java.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionService {
    @Autowired
    PermissionDAO permissionDAO;

    @Autowired
    UserService userService;

    public List<Permission> list() {
        return permissionDAO.findAll();
    }

    public List<Permission> listByUser(User user){
        return permissionDAO.findByUser(user);
    }

    public boolean needInterceptor(String requestURI) {
        List<Permission> ps = list();
        for (Permission p : ps) {
            if (p.getUrl().equals(requestURI))
                return true;
        }
        return false;
    }


    public Set<String> listPermissionURLs(String userName) {
        Set<String> result = new HashSet<>();

        User user = userService.getByName(userName);
        List<Permission> permissions = listByUser(user);

        for (Permission permission:permissions){
            result.add(permission.getUrl());
        }

        return result;
    }
}
