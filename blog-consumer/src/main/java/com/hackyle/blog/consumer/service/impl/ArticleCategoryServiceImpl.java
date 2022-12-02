package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.consumer.entity.ArticleCategoryEntity;
import com.hackyle.blog.consumer.mapper.ArticleCategoryMapper;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;
import com.hackyle.blog.consumer.service.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategoryEntity>
    implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public Map<Long, List<ArticleCategoryPo>> selectByArticleIds(List<Long> articleIds) {
        List<ArticleCategoryPo> articleAuthorPoList = articleCategoryMapper.selectByArticleIds(articleIds);

        return articleAuthorPoList.stream().collect(Collectors.groupingBy(ArticleCategoryPo::getArticleId));
    }
}




