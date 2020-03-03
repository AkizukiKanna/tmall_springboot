package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    ProductImageService productImageService;

    @GetMapping("/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid,
                                        @RequestParam(value = "start",defaultValue = "0") int start,
                                        @RequestParam(value = "size",defaultValue = "5") int size,
                                        @RequestParam(value = "navigatePages",defaultValue = "5") int navigatePages)throws Exception{
        Page4Navigator<Product> page = productService.list(cid, start, size, navigatePages);
        productImageService.setFirstProdutImages(page.getContent());

        return page;
    }

    @PostMapping("/products")
    public Object add(@RequestBody Product bean) throws Exception{
        //注意创建一个日期对象
        bean.setCreateDate(new Date());
        productService.add(bean);
        return bean;
    }

    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id") int id) throws Exception{
        productService.delete(id);
        return null;
    }

    @GetMapping("/products/{id}")
    public Object get(@PathVariable("id") int id)throws Exception{
        return productService.get(id);
    }

    @PutMapping("/products")
    public Object update(@RequestBody Product bean) throws Exception{
        productService.update(bean);
        return bean;
    }

}
