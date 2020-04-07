package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Permission;
import com.how2java.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionDAO extends JpaRepository<Permission,Integer> {
    List<Permission> findByUser(User user);
}
