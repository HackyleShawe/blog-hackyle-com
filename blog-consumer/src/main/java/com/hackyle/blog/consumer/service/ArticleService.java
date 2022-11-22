package com.hackyle.blog.consumer.service;

import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.vo.ArticleVo;


public interface ArticleService {

    PageResponseDto<ArticleVo> pageByNum(Integer pageNum);

    ArticleVo articleDetail(String uri);
}
