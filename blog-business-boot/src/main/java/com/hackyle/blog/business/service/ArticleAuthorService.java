package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.entity.ArticleAuthorEntity;
import com.hackyle.blog.business.vo.AuthorVo;

import java.util.List;
import java.util.Map;

public interface ArticleAuthorService extends IService<ArticleAuthorEntity> {
    void batchInsert(long articleId, String authorIds);

    void batchDelByArticleIds(List<Long> idList);

    void update(long articleId, String authorIds);

    void batchUpdate(long articleId, String authorIds);


    Map<Long, List<AuthorVo>> selectByArticleIds(List<Long> articleIds);

}
