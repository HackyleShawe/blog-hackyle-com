package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.consumer.entity.ArticleAuthorEntity;
import com.hackyle.blog.consumer.mapper.ArticleAuthorMapper;
import com.hackyle.blog.consumer.po.ArticleAuthorPo;
import com.hackyle.blog.consumer.service.ArticleAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleAuthorServiceImpl extends ServiceImpl<ArticleAuthorMapper, ArticleAuthorEntity>
    implements ArticleAuthorService {

    @Autowired
    private ArticleAuthorMapper articleAuthorMapper;

    /**
     * 获取多个文章的作者信息
     * @param articleIds 多个文章
     * @return Map<文章ID，作者信息LIst>
     */
    @Override
    public Map<Long, List<ArticleAuthorPo>> selectByArticleIds(List<Long> articleIds) {

        List<ArticleAuthorPo> articleAuthorPoList = articleAuthorMapper.selectByArticleIds(articleIds);

        return articleAuthorPoList.stream().collect(Collectors.groupingBy(ArticleAuthorPo::getArticleId));
    }
}




