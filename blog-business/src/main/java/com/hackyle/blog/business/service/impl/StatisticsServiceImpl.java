package com.hackyle.blog.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.entity.CategoryEntity;
import com.hackyle.blog.business.entity.CommentEntity;
import com.hackyle.blog.business.entity.TagEntity;
import com.hackyle.blog.business.mapper.ArticleMapper;
import com.hackyle.blog.business.mapper.CategoryMapper;
import com.hackyle.blog.business.mapper.CommentMapper;
import com.hackyle.blog.business.mapper.TagMapper;
import com.hackyle.blog.business.service.StatisticsService;
import com.hackyle.blog.business.vo.StatisticsCountNumberVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StatisticsServiceImpl implements StatisticsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public StatisticsCountNumberVo countNumber() {
        QueryWrapper<ArticleEntity> queryArticle = new QueryWrapper<>();
        queryArticle.lambda().eq(ArticleEntity::getDeleted, 0);
        Integer articles = articleMapper.selectCount(queryArticle);

        QueryWrapper<CategoryEntity> queryCategory = new QueryWrapper<>();
        queryCategory.lambda().eq(CategoryEntity::getDeleted, 0);
        Integer categories = categoryMapper.selectCount(queryCategory);

        QueryWrapper<TagEntity> queryTag = new QueryWrapper<>();
        queryTag.lambda().eq(TagEntity::getDeleted, 0);
        Integer tags = tagMapper.selectCount(queryTag);

        QueryWrapper<CommentEntity> queryComment = new QueryWrapper<>();
        queryComment.lambda().eq(CommentEntity::getDeleted, 0);
        Integer comments = commentMapper.selectCount(queryComment);

        StatisticsCountNumberVo numberVo = new StatisticsCountNumberVo();
        numberVo.setArticles(articles);
        numberVo.setCategories(categories);
        numberVo.setTags(tags);
        numberVo.setComments(comments);

        return numberVo;
    }

}
