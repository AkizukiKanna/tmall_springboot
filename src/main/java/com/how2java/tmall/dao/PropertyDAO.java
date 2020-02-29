package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDAO extends JpaRepository<Property,Integer> {

    //条件查询 使用 JPA 规定的形式 findBy+参数名(参数)
    Page<Property> findByCategory(Category category, Pageable pageable);
}
