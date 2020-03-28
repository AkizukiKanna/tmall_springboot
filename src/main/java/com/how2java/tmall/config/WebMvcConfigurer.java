package com.how2java.tmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.how2java.tmall.interceptor.LoginInterceptor;
import com.how2java.tmall.interceptor.OtherInterceptor;

@Configuration
class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }
    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
//        registry.addInterceptor(getOtherIntercepter())
//                .addPathPatterns("/**");
//        registry.addInterceptor(getLoginIntercepter())
//                .addPathPatterns("/**");
        String[] exclude = new String[] {"/js/**","/img/**","/css/**","/webapp/**"};
        registry.addInterceptor(getLoginIntercepter()).addPathPatterns("/**").excludePathPatterns(exclude);
        registry.addInterceptor(getOtherIntercepter()).addPathPatterns("/**").excludePathPatterns(exclude);
    }
}