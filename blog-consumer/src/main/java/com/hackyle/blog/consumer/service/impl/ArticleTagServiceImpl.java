package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.consumer.entity.ArticleTagEntity;
import com.hackyle.blog.consumer.mapper.ArticleTagMapper;
import com.hackyle.blog.consumer.po.ArticleTagPo;
import com.hackyle.blog.consumer.service.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTagEntity>
    implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public Map<Long, List<ArticleTagPo>> selectByArticleIds(List<Long> articleIds) {
        List<ArticleTagPo> articleAuthorPoList = articleTagMapper.selectByArticleIds(articleIds);

        return articleAuthorPoList.stream().collect(Collectors.groupingBy(ArticleTagPo::getArticleId));
    }
}




