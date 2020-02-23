package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    //navigatePages分页导航栏要显示出的页的数量
    public Page4Navigator<Category> list(int start, int size, int navigatePages){
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        //Pageable pageable = new PageRequest(start, size,sort);过时。
        //PageRequest存放分页信息，第一个参数page(start)是页码，就是本次查询要查询哪一页，默认是从0开始的。
        Pageable pageable = PageRequest.of(start, size, sort);
        //??需要把查询结果分页，所以使用findAll(Pageable)
        Page pageFromJPA = categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public List<Category> list(){
        //创建一个 Sort 对象，表示通过 id 倒排序。2.0版本需要用Sort.by(),原因是Sort类的构造方法被私有化
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        //??只需要列出所有的查询结果，不需要分页，所以使用findAll(Sort)
        return categoryDAO.findAll(sort);
    }

    public void add(Category bean){
        categoryDAO.save(bean);
    }
}
