package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ArticleAddDto;
import com.hackyle.blog.business.entity.ArticleCategoryEntity;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.qo.ArticleQo;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.vo.ArticleVo;


public interface ArticleService extends IService<ArticleEntity> {

    ApiResponse<String> add(ArticleAddDto articleAddDto) throws Exception;

    ApiResponse<String> del(String ids);

    ApiResponse<String> delReal(String ids);

    ApiResponse<String> update(ArticleAddDto articleUpdateDto);

    ArticleVo fetch(long idd);

    PageResponseDto<ArticleVo> fetchList(PageRequestDto<ArticleQo> pageRequestDto);
}