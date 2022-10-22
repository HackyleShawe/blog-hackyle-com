package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.entity.ArticleTagEntity;
import com.hackyle.blog.business.vo.TagVo;

import java.util.List;
import java.util.Map;

public interface ArticleTagService extends IService<ArticleTagEntity> {
    void batchInsert(long articleId, String tagIds);

    void batchDelByArticleIds(List<Long> idList);

    void batchUpdate(long articleId, String tagIds);

    Map<Long, List<TagVo>> selectByArticleIds(List<Long> articleIds);

}
