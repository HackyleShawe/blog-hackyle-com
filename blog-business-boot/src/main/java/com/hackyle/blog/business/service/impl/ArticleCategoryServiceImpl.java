package com.hackyle.blog.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.entity.ArticleCategoryEntity;
import com.hackyle.blog.business.mapper.ArticleCategoryMapper;
import com.hackyle.blog.business.po.ArticleCategoryPo;
import com.hackyle.blog.business.service.ArticleCategoryService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.vo.CategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategoryEntity>
    implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public void batchInsert(long articleId, String categoryIds) {
        if(articleId < 0 || StringUtils.isBlank(categoryIds)) {
            return;
        }

        String[] categoryIdArr = categoryIds.split(",");
        int articleCategoryInserted = articleCategoryMapper.batchInsert(articleId, categoryIdArr);
        if(articleCategoryInserted < 1) {
            throw new RuntimeException("文章分类插入失败！");
        }
    }


    @Override
    public void batchDelByArticleIds(List<Long> articleIds) {
        if(articleIds.isEmpty()) {
            return;
        }
        articleCategoryMapper.batchDelByArticleIds(articleIds);
    }

    @Override
    public void batchUpdate(long articleId, String categoryIds) {
        if(articleId < 0 || StringUtils.isBlank(categoryIds)) {
            return;
        }

        String[] categoryIdArr = categoryIds.split(",");

        UpdateWrapper<ArticleCategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleCategoryEntity::getArticleId, articleId);

        for (String id : categoryIdArr) {
            ArticleCategoryEntity articleCategoryEntity = new ArticleCategoryEntity();
            articleCategoryEntity.setCategoryId(Long.parseLong(id));

            articleCategoryMapper.update(articleCategoryEntity, updateWrapper);
        }
    }

    @Override
    public Map<Long, List<CategoryVo>> selectByArticleIds(List<Long> articleIds) {
        Map<Long, List<CategoryVo>> resultMap = new HashMap<>();

        List<ArticleCategoryPo> articleAuthorPoList = articleCategoryMapper.selectByArticleIds(articleIds);

        for (Long id : articleIds) {
            List<CategoryVo> authorVoList = new ArrayList<>();

            for (ArticleCategoryPo articleCategoryPo : articleAuthorPoList) {
                if(id.equals(articleCategoryPo.getArticleId())) {
                    CategoryVo categoryVo = BeanCopyUtils.copy(articleCategoryPo, CategoryVo.class);
                    categoryVo.setId(articleCategoryPo.getCategoryId());
                    authorVoList.add(categoryVo);
                }
            }
            resultMap.put(id, authorVoList);
        }

        return resultMap;
    }
}




