package com.hackyle.blog.business.service;

import com.hackyle.blog.business.qo.ArticleAccessLogQo;
import com.hackyle.blog.business.vo.ArticleAccessLogVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

public interface LoggerService {
    PageResponseDto<ArticleAccessLogVo> articleAccess(PageRequestDto<ArticleAccessLogQo> pageRequestDto);
}
