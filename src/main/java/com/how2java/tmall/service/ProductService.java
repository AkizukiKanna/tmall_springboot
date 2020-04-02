package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDAO;
import com.how2java.tmall.dao.ProductDAO;
import com.how2java.tmall.es.ProductESDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.SpringContextUtil;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames="products")
public class ProductService {
    @Autowired
    ProductESDAO productESDAO;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    ProductImageService productImageService;

    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages){
        Category category = categoryDAO.findById(cid).get();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        Page<Product> pageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    @CacheEvict(allEntries=true)
    public void add(Product bean){
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id){
        productDAO.deleteById(id);
        productESDAO.deleteById(id);
    }

    @Cacheable(key="'products-one-'+ #p0")
    public Product get(int id){
        return productDAO.findById(id).get();
    }

    @CacheEvict(allEntries=true)
    public void update(Product bean){
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    public void fill(List<Category> categories){
        for (Category category:categories){
            fill(category);
        }
    }

    public void fill(Category category){
//        如果在ProductService的一个方法里，调用另一个 缓存管理 的方法，不能够直接调用，需要通过一个工具，再拿一次 ProductService， 然后再调用。
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

//    public List<Product> search(String keyword, int start, int size){
//        Sort sort = Sort.by(Sort.Direction.DESC,"id");
//        Pageable pageable  = PageRequest.of(start, size, sort);
//        List<Product> products = productDAO.findByNameLike("%"+keyword+"%", pageable);
//        return products;
//    }
public List<Product> search(String keyword, int start, int size) {
    initDatabase2ES();
    QueryBuilder queryBuilder=null;

    FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
            new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchPhraseQuery("name", keyword),
            ScoreFunctionBuilders.weightFactorFunction(100))};

    FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(filterFunctionBuilders)
//            .add(QueryBuilders.matchPhraseQuery("name", keyword),
//                    ScoreFunctionBuilders.weightFactorFunction(100))
            .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
            .setMinScore(10);
    Sort sort  = Sort.by(Sort.Direction.DESC,"id");
    Pageable pageable = PageRequest.of(start, size,sort);
    SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withPageable(pageable)
            .withQuery(functionScoreQueryBuilder).build();
    Page<Product> page = productESDAO.search(searchQuery);
    return page.getContent();
}

    private void initDatabase2ES() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page =productESDAO.findAll(pageable);
        if(page.getContent().isEmpty()) {
            List<Product> products= productDAO.findAll();
            for (Product product : products) {
                productESDAO.save(product);
            }
        }
    }
}
