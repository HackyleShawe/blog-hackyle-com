package com.hackyle.blog.consumer.service;

import com.hackyle.blog.consumer.dto.ArticleAccessLogDto;

public interface LoggerService {
    void insertArticleAccessLog(ArticleAccessLogDto logDto);
}
