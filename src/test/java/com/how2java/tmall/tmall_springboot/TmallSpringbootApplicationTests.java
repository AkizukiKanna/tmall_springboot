package com.how2java.tmall.tmall_springboot;

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.web.CategoryController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@SpringBootTest
class TmallSpringbootApplicationTests {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryController categoryController;

    @Test
    void contextLoads() {
        System.out.println(categoryService.list(1,5,3));
    }





}
