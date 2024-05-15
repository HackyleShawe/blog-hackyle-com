package com.hackyle.blog.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hackyle.blog"}) //这样才能扫描到blog-common包中含有注解的类
public class BlogConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogConsumerApp.class, args);
    }
}
