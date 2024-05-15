package com.hackyle.blog.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@ConfigurationPropertiesScan //从YML文件中载入数据
@SpringBootApplication
@ComponentScan(basePackages = {"com.hackyle.blog"}) //这样才能扫描到blog-common包中含有注解的类
public class BlogBusinessApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogBusinessApp.class, args);
    }
}
