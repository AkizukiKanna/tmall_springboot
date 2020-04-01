package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@CacheConfig(cacheNames="categories")
public class CategoryService {

    @Autowired
    CategoryDAO categoryDAO;

    //navigatePages分页导航栏要显示出的页的数量
    @Cacheable(key="'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages){
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        //Pageable pageable = new PageRequest(start, size,sort);过时。
        //PageRequest存放分页信息，第一个参数page(start)是页码，就是本次查询要查询哪一页，默认是从0开始的。
        Pageable pageable = PageRequest.of(start, size, sort);
        //??需要把查询结果分页，所以使用findAll(Pageable)
        Page pageFromJPA = categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    @Cacheable(key="'categories-all'")
    public List<Category> list(){
        //创建一个 Sort 对象，表示通过 id 倒排序。2.0版本需要用Sort.by(),原因是Sort类的构造方法被私有化
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        //??只需要列出所有的查询结果，不需要分页，所以使用findAll(Sort)
        return categoryDAO.findAll(sort);
    }

    @CacheEvict(allEntries=true)
//  @CachePut(key="'category-one-'+ #p0")
    public void add(Category bean){
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
//  @CacheEvict(key="'category-one-'+ #p0")
    public void delete(int id){
        categoryDAO.deleteById(id);
    }

    @Cacheable(key="'categories-one-'+ #p0")
    public Category get(int id){
        Optional<Category> byId = categoryDAO.findById(id);
        Category bean = byId.get();
        return bean;
    }

    @CacheEvict(allEntries=true)
//  @CachePut(key="'category-one-'+ #p0")
    public void update(Category bean){
        categoryDAO.save(bean);
    }

    public void removeCategoryFromProduct(List<Category> cs){
        for (Category c:cs) {
            removeCategoryFromProduct(c);
        }
    }

    public void removeCategoryFromProduct(Category category){
        List<Product> products = category.getProducts();
        if (null!=products){
            for (Product product:products){
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null!=productsByRow){
            for (List<Product> ps : productsByRow) {
                for (Product p: ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
