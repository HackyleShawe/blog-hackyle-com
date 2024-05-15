package com.hackyle.blog.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.consumer.entity.ArticleCategoryEntity;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;
import com.hackyle.blog.consumer.qo.CategoryQo;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.consumer.vo.CategoryVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

import java.util.List;
import java.util.Map;

public interface ArticleCategoryService extends IService<ArticleCategoryEntity> {

    Map<Long, List<ArticleCategoryPo>> selectByArticleIds(List<Long> articleIds);

    List<CategoryVo> selectCategory();

    PageResponseDto<ArticleVo> selectArticleByCategory(PageRequestDto<CategoryQo> requestDto);

}
