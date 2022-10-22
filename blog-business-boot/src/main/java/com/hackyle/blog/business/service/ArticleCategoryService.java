package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.entity.ArticleCategoryEntity;
import com.hackyle.blog.business.vo.CategoryVo;

import java.util.List;
import java.util.Map;

public interface ArticleCategoryService extends IService<ArticleCategoryEntity> {
    void batchInsert(long articleId, String categoryIds);

    void batchDelByArticleIds(List<Long> idList);

    void batchUpdate(long articleId, String categoryIds);

    Map<Long, List<CategoryVo>> selectByArticleIds(List<Long> articleIds);

}
