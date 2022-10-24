package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.entity.ArticleTagEntity;
import com.hackyle.blog.business.po.ArticleTagPo;

import java.util.List;
import java.util.Map;

public interface ArticleTagService extends IService<ArticleTagEntity> {
    void batchInsert(long articleId, List<Long> tagIds);

    void batchDelByArticleIds(List<Long> articleIds);

    void update(long articleId, List<Long> tagIds);

    Map<Long, List<ArticleTagPo>> selectByArticleIds(List<Long> articleIds);

}
