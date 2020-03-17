package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    User findByName(String name);

    //find是返回一个集合， get 是返回一个对象。
    User getByNameAndPassword(String name, String password);
}
