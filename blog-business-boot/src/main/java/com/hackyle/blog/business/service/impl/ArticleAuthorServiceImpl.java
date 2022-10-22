package com.hackyle.blog.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.entity.ArticleAuthorEntity;
import com.hackyle.blog.business.entity.ArticleAuthorEntity;
import com.hackyle.blog.business.mapper.ArticleAuthorMapper;
import com.hackyle.blog.business.po.ArticleAuthorPo;
import com.hackyle.blog.business.service.ArticleAuthorService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.vo.AuthorVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleAuthorServiceImpl extends ServiceImpl<ArticleAuthorMapper, ArticleAuthorEntity>
    implements ArticleAuthorService {

    @Autowired
    private ArticleAuthorMapper articleAuthorMapper;

    @Override
    public void batchInsert(long articleId, String authorIds) {
        if(articleId < 0 || StringUtils.isBlank(authorIds)) {
            return;
        }

        int articleAuthorInserted = articleAuthorMapper.batchInsert(articleId, authorIds.split(","));
        if(articleAuthorInserted < 1) {
            throw new RuntimeException("文章作者插入失败！");
        }
    }


    @Override
    public void batchDelByArticleIds(List<Long> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }
        articleAuthorMapper.batchDelByArticleIds(articleIds);
    }


    @Override
    public void update(long articleId, String authorIds) {
        if(articleId < 0 || StringUtils.isBlank(authorIds)) {
            return;
        }

        int articleAuthorInserted = articleAuthorMapper.batchInsert(articleId, authorIds.split(","));
        if(articleAuthorInserted < 1) {
            throw new RuntimeException("文章作者更新失败！");
        }
    }

    @Override
    public void batchUpdate(long articleId, String authorIds) {
        if(articleId < 0 || StringUtils.isBlank(authorIds)) {
            return;
        }

        String[] categoryIdArr = authorIds.split(",");

        UpdateWrapper<ArticleAuthorEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleAuthorEntity::getArticleId, articleId);

        for (String id : categoryIdArr) {
            ArticleAuthorEntity articleAuthorEntity = new ArticleAuthorEntity();
            articleAuthorEntity.setAuthorId(Long.parseLong(id));

            articleAuthorMapper.update(articleAuthorEntity, updateWrapper);
        }
    }


    @Override
    public Map<Long, List<AuthorVo>> selectByArticleIds(List<Long> articleIds) {
        Map<Long, List<AuthorVo>> resultMap = new HashMap<>();

        List<ArticleAuthorPo> articleAuthorPoList = articleAuthorMapper.selectByArticleIds(articleIds);

        for (Long id : articleIds) {
            List<AuthorVo> authorVoList = new ArrayList<>();

            for (ArticleAuthorPo articleAuthorPo : articleAuthorPoList) {
                if(id.equals(articleAuthorPo.getArticleId())) {
                    AuthorVo authorVo = BeanCopyUtils.copy(articleAuthorPo, AuthorVo.class);
                    authorVo.setId(articleAuthorPo.getAuthorId());
                    authorVoList.add(authorVo);
                }
            }
            resultMap.put(id, authorVoList);
        }

        return resultMap;
    }
}




