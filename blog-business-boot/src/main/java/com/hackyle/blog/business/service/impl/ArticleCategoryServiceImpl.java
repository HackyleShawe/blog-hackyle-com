package com.hackyle.blog.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.entity.ArticleCategoryEntity;
import com.hackyle.blog.business.mapper.ArticleCategoryMapper;
import com.hackyle.blog.business.po.ArticleCategoryPo;
import com.hackyle.blog.business.service.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategoryEntity>
    implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Transactional
    @Override
    public void batchInsert(long articleId, List<Long> categoryIds) {
        if(articleId < 0 || categoryIds == null || categoryIds.size() < 1) {
            return;
        }

        int articleCategoryInserted = articleCategoryMapper.batchInsert(articleId, categoryIds);
        if(articleCategoryInserted < 1) {
            throw new RuntimeException("文章分类插入失败！");
        }
    }

    @Transactional
    @Override
    public void batchDelByArticleIds(List<Long> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }
        articleCategoryMapper.batchDelByArticleIds(articleIds);
    }

    @Transactional
    @Override
    public void update(long articleId, List<Long> categoryIds) {
        if(articleId < 0 || categoryIds == null || categoryIds.size() < 1) {
            return;
        }

        UpdateWrapper<ArticleCategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleCategoryEntity::getArticleId, articleId);
        articleCategoryMapper.delete(updateWrapper);


        this.batchInsert(articleId, categoryIds);
    }

    @Override
    public Map<Long, List<ArticleCategoryPo>> selectByArticleIds(List<Long> articleIds) {
        List<ArticleCategoryPo> articleAuthorPoList = articleCategoryMapper.selectByArticleIds(articleIds);

        return articleAuthorPoList.stream().collect(Collectors.groupingBy(ArticleCategoryPo::getArticleId));
    }
}




