package com.hackyle.blog.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.entity.ArticleTagEntity;
import com.hackyle.blog.business.mapper.ArticleTagMapper;
import com.hackyle.blog.business.po.ArticleTagPo;
import com.hackyle.blog.business.service.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTagEntity>
    implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Transactional
    @Override
    public void batchInsert(long articleId, List<Long> tagIds) {
        if(articleId < 0 || null == tagIds || tagIds.size() < 1) {
            return;
        }

        int articleTagInserted = articleTagMapper.batchInsert(articleId, tagIds);
        if(articleTagInserted < 1) {
            throw new RuntimeException("文章标签插入失败！");
        }
    }

    @Transactional
    @Override
    public void batchDelByArticleIds(List<Long> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }
        articleTagMapper.batchDelByArticleIds(articleIds);
    }

    @Transactional
    @Override
    public void update(long articleId, List<Long> tagIds) {
        if(articleId < 0) {
            return;
        }

        //检查一下是否与以前的一样，如果一样，则不用再执行删除、插入了
        QueryWrapper<ArticleTagEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleTagEntity::getArticleId, articleId);
        List<ArticleTagEntity> existTags = articleTagMapper.selectList(queryWrapper);
        List<Long> existTagIds = existTags.stream().map(ArticleTagEntity::getTagId).collect(Collectors.toList());
        if(tagIds != null && CollectionUtil.isEqualList(tagIds, existTagIds)) {
            return;
        }

        UpdateWrapper<ArticleTagEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleTagEntity::getArticleId, articleId);
        articleTagMapper.delete(updateWrapper);

        if(tagIds != null && !tagIds.isEmpty()) {
            this.batchInsert(articleId, tagIds);
        }
    }

    @Override
    public Map<Long, List<ArticleTagPo>> selectByArticleIds(List<Long> articleIds) {
        List<ArticleTagPo> articleAuthorPoList = articleTagMapper.selectByArticleIds(articleIds);

        return articleAuthorPoList.stream().collect(Collectors.groupingBy(ArticleTagPo::getArticleId));
    }
}




