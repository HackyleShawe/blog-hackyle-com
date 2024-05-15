package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ArticleAddDto;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.qo.ArticleQo;
import com.hackyle.blog.business.vo.ArticleVo;
import com.hackyle.blog.business.vo.AuthorVo;
import com.hackyle.blog.business.vo.CategoryVo;
import com.hackyle.blog.business.vo.TagVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

import java.util.List;

public interface ArticleService extends IService<ArticleEntity> {

    ApiResponse<String> add(ArticleAddDto articleAddDto) throws Exception;

    ApiResponse<String> del(String ids);

    ApiResponse<String> delReal(String ids);

    ApiResponse<String> update(ArticleAddDto articleUpdateDto);

    ArticleVo fetch(String id);

    PageResponseDto<ArticleVo> fetchList(PageRequestDto<ArticleQo> pageRequestDto);

    List<AuthorVo> fetchAuthor(String articleId);

    List<CategoryVo> fetchCategory(String articleId);

    List<TagVo> fetchTag(String articleId);
}