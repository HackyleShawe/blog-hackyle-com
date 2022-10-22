package com.hackyle.blog.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.entity.ArticleTagEntity;
import com.hackyle.blog.business.entity.ArticleTagEntity;
import com.hackyle.blog.business.mapper.ArticleTagMapper;
import com.hackyle.blog.business.po.ArticleTagPo;
import com.hackyle.blog.business.service.ArticleTagService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTagEntity>
    implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public void batchInsert(long articleId, String tagIds) {
        if(articleId < 0 || StringUtils.isBlank(tagIds)) {
            return;
        }

        String[] tagIdArr = tagIds.split(",");
        int articleTagInserted = articleTagMapper.batchInsert(articleId, tagIdArr);
        if(articleTagInserted < 1) {
            throw new RuntimeException("文章标签插入失败！");
        }
    }


    @Override
    public void batchDelByArticleIds(List<Long> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }
        articleTagMapper.batchDelByArticleIds(articleIds);
    }

    @Override
    public void batchUpdate(long articleId, String tagIds) {
        if(articleId < 0 || StringUtils.isBlank(tagIds)) {
            return;
        }

        String[] categoryIdArr = tagIds.split(",");

        UpdateWrapper<ArticleTagEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleTagEntity::getArticleId, articleId);

        for (String id : categoryIdArr) {
            ArticleTagEntity articleTagEntity = new ArticleTagEntity();
            articleTagEntity.setTagId(Long.parseLong(id));

            articleTagMapper.update(articleTagEntity, updateWrapper);
        }
    }

    @Override
    public Map<Long, List<TagVo>> selectByArticleIds(List<Long> articleIds) {
        Map<Long, List<TagVo>> resultMap = new HashMap<>();

        List<ArticleTagPo> articleAuthorPoList = articleTagMapper.selectByArticleIds(articleIds);

        for (Long id : articleIds) {
            List<TagVo> authorVoList = new ArrayList<>();

            for (ArticleTagPo articleTagPo : articleAuthorPoList) {
                if(id.equals(articleTagPo.getArticleId())) {
                    TagVo tagVo = BeanCopyUtils.copy(articleTagPo, TagVo.class);
                    tagVo.setId(articleTagPo.getTagId());
                    authorVoList.add(tagVo);
                }
            }
            resultMap.put(id, authorVoList);
        }

        return resultMap;
    }
}




