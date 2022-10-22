package com.hackyle.blog.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan //从YML文件中载入数据
@SpringBootApplication
public class BlogBusinessApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogBusinessApp.class, args);
    }
}
