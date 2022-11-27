package com.hackyle.blog.consumer.service.impl;

import com.hackyle.blog.consumer.dto.ArticleAccessLogDto;
import com.hackyle.blog.consumer.entity.ArticleAccessEntity;
import com.hackyle.blog.consumer.mapper.ArticleAccessMapper;
import com.hackyle.blog.consumer.service.LoggerService;
import com.hackyle.blog.consumer.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggerServiceImpl implements LoggerService {
    @Autowired
    private ArticleAccessMapper articleAccessMapper;

    @Override
    public void insertArticleAccessLog(ArticleAccessLogDto logDto) {
        ArticleAccessEntity articleAccessEntity = BeanCopyUtils.copy(logDto, ArticleAccessEntity.class);
        articleAccessMapper.insert(articleAccessEntity);
    }
}
