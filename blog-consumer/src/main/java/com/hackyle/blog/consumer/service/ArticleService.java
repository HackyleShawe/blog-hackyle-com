package com.hackyle.blog.consumer.service;

import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.vo.ArticleVo;


public interface ArticleService {

    ArticleVo articleDetail(String uri);

    PageResponseDto<ArticleVo> pageByNum(Integer pageNum);
}