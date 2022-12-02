package com.hackyle.blog.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.consumer.entity.ArticleCategoryEntity;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;

import java.util.List;
import java.util.Map;

public interface ArticleCategoryService extends IService<ArticleCategoryEntity> {

    Map<Long, List<ArticleCategoryPo>> selectByArticleIds(List<Long> articleIds);

}
