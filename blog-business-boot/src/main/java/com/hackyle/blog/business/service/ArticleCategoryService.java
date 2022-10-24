package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.entity.ArticleCategoryEntity;
import com.hackyle.blog.business.po.ArticleCategoryPo;

import java.util.List;
import java.util.Map;

public interface ArticleCategoryService extends IService<ArticleCategoryEntity> {
    void batchInsert(long articleId, List<Long> categoryIds);

    void batchDelByArticleIds(List<Long> articleIds);

    void update(long articleId, List<Long> categoryIds);

    Map<Long, List<ArticleCategoryPo>> selectByArticleIds(List<Long> articleIds);

}
