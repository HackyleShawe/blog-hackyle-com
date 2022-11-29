package com.hackyle.blog.consumer.common.config;

import com.hackyle.blog.consumer.common.interceptor.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AccessInterceptor accessInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //请求访问拦截器
        registry.addInterceptor(accessInterceptor)
                //拦截这些请求走MvcInterceptor中的逻辑
                .addPathPatterns("/**")
                //不需要走MvcInterceptor中的逻辑
                .excludePathPatterns(
                        "/css/**", //排除调静态资源
                        "/img/**",
                        "/js/**",
                        "/plugin/**",
                        "/error"
                );

        //registry.addInterceptor(accessInterceptor)
        //        //拦截这些请求走MvcInterceptor中的逻辑
        //        .addPathPatterns("/**")
        //        //不需要走MvcInterceptor中的逻辑
        //        .excludePathPatterns(
        //                "/css/**", //排除调静态资源
        //                "/img/**",
        //                "/js/**",
        //                "/plugin/**",
        //                "/error"
        //        );
    }

}
