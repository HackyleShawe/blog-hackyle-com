package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.entity.ArticleAuthorEntity;
import com.hackyle.blog.business.po.ArticleAuthorPo;

import java.util.List;
import java.util.Map;

public interface ArticleAuthorService extends IService<ArticleAuthorEntity> {
    void batchInsert(long articleId, List<Long> authorIds);

    void batchDelByArticleIds(List<Long> idList);

    void update(long articleId, List<Long> authorIds);

    Map<Long, List<ArticleAuthorPo>> selectByArticleIds(List<Long> articleIds);

}
