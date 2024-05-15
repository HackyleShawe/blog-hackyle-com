package com.hackyle.blog.consumer.service;

import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

public interface ArticleService {

    PageResponseDto<ArticleVo> pageByNum(PageRequestDto<String> pageRequestDto);

    ArticleVo articleDetail(String uri);
}
