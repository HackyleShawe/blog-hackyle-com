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
    public Integer countArticles() {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleEntity::getDeleted, 0);
        return articleMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer countCategories() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CategoryEntity::getDeleted, 0);
        return categoryMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer countTags() {
        QueryWrapper<TagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TagEntity::getDeleted, 0);
        return tagMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer countComments() {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CommentEntity::getDeleted, 0);
        return commentMapper.selectCount(queryWrapper);
    }


}
