package com.how2java.tmall.web;
 
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.UserService;
import com.how2java.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class ForeRESTController {
    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
 
    @GetMapping("/forehome")
    public Object home() {
        List<Category> cs= categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

    /**
     * 注： 为什么要用 HtmlUtils.htmlEscape？ 因为有些同学在恶意注册的时候，会使用诸如 <script>alert('papapa')</script> 这样的名称，会导致网页打开就弹出一个对话框。 那么在转义之后，就没有这个问题了。
     * 注： 密码为什么没有加密？ User表还有个 salt字段，为什么没有使用。 咳咳。。。 是这样的，目前这里仅仅实现简单的用户注册功能，后续还在这个基础上改造成用 Shiro 来实现用户验证，加密等等。
     * 注： Result 这个类，第一次使用，是在订单管理 发货功能讲解里用到的，当发货成功后，会返回 Result对象。 Result 对象是一种常见的 RESTFUL 风格返回的 json 格式，里面可以有错误代码，错误信息和数据。 这样就比起以前那样，仅仅返回数据附加了更多的信息，方便前端人员识别和显示给用户可识别信息。
     */
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user){
        String name =  user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if(exist){
            String message ="用户名已经被使用,不能使用";
            return Result.fail(message);
        }

        user.setPassword(password);

        userService.add(user);

        return Result.success();
    }


    /**
     * 注 为什么要用 HtmlUtils.htmlEscape？ 因为注册的时候，ForeRESTController.register()，就进行了转义，所以这里也需要转义。
     * 有些同学在恶意注册的时候，会使用诸如 <script>alert('papapa')</script> 这样的名称，会导致网页打开就弹出一个对话框。 那么在转义之后，就没有这个问题了。
     */
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session){
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user = userService.get(name, userParam.getPassword());
        if (null!=user){
            session.setAttribute("user",user);
            return Result.success();
        }else {
            return Result.fail("账号密码错误");
        }
    }
}