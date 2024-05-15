package com.hackyle.blog.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.entity.ArticleAuthorEntity;
import com.hackyle.blog.business.mapper.ArticleAuthorMapper;
import com.hackyle.blog.business.po.ArticleAuthorPo;
import com.hackyle.blog.business.service.ArticleAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleAuthorServiceImpl extends ServiceImpl<ArticleAuthorMapper, ArticleAuthorEntity>
    implements ArticleAuthorService {

    @Autowired
    private ArticleAuthorMapper articleAuthorMapper;

    @Transactional
    @Override
    public void batchInsert(long articleId, List<Long> authorIds) {
        if(articleId < 0 || authorIds == null || authorIds.size() < 1) {
            return;
        }

        int articleAuthorInserted = articleAuthorMapper.batchInsert(articleId, authorIds);
        if(articleAuthorInserted < 1) {
            throw new RuntimeException("文章作者插入失败！");
        }
    }

    @Transactional
    @Override
    public void batchDelByArticleIds(List<Long> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }
        articleAuthorMapper.batchDelByArticleIds(articleIds);
    }


    /**
     * 更新操作：删除以前的关联，插入新的关联
     */
    @Transactional
    @Override
    public void update(long articleId, List<Long> authorIds) {
        if(articleId < 0) {
            return;
        }

        //检查一下是否与以前的一样，如果一样，则不用再执行删除、插入了
        QueryWrapper<ArticleAuthorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleAuthorEntity::getArticleId, articleId);
        List<ArticleAuthorEntity> existAuthors = articleAuthorMapper.selectList(queryWrapper);
        List<Long> existAuthorIds = existAuthors.stream().map(ArticleAuthorEntity::getAuthorId).collect(Collectors.toList());
        if(authorIds != null && CollectionUtil.isEqualList(authorIds, existAuthorIds)) {
            return;
        }

        UpdateWrapper<ArticleAuthorEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleAuthorEntity::getArticleId, articleId);
        articleAuthorMapper.delete(updateWrapper);

        if(authorIds != null && !authorIds.isEmpty()) {
            this.batchInsert(articleId, authorIds);
        }
    }

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




