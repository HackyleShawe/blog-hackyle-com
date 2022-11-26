package com.hackyle.blog.business.service;

import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.ArticleAccessLogQo;
import com.hackyle.blog.business.vo.ArticleAccessLogVo;

public interface LoggerService {
    PageResponseDto<ArticleAccessLogVo> articleAccess(PageRequestDto<ArticleAccessLogQo> pageRequestDto);
}
