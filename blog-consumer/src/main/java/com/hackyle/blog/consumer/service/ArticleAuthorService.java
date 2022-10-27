package com.hackyle.blog.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.consumer.entity.ArticleAuthorEntity;
import com.hackyle.blog.consumer.po.ArticleAuthorPo;

import java.util.List;
import java.util.Map;

public interface ArticleAuthorService extends IService<ArticleAuthorEntity> {
    Map<Long, List<ArticleAuthorPo>> selectByArticleIds(List<Long> articleIds);

}
