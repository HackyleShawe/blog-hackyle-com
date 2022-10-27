package com.hackyle.blog.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.consumer.entity.ArticleTagEntity;
import com.hackyle.blog.consumer.po.ArticleTagPo;

import java.util.List;
import java.util.Map;

public interface ArticleTagService extends IService<ArticleTagEntity> {
    Map<Long, List<ArticleTagPo>> selectByArticleIds(List<Long> articleIds);

}
