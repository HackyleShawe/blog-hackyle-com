package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.consumer.entity.ArticleCategoryEntity;
import com.hackyle.blog.consumer.entity.ArticleEntity;
import com.hackyle.blog.consumer.entity.CategoryEntity;
import com.hackyle.blog.consumer.mapper.ArticleCategoryMapper;
import com.hackyle.blog.consumer.mapper.ArticleMapper;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;
import com.hackyle.blog.consumer.qo.CategoryQo;
import com.hackyle.blog.consumer.service.ArticleCategoryService;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.consumer.vo.CategoryVo;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.PaginationUtils;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategoryEntity>
    implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Value("${blog.article-path}")
    private String articlePath;

    @Override
    public Map<Long, List<ArticleCategoryPo>> selectByArticleIds(List<Long> articleIds) {
        List<ArticleCategoryPo> articleAuthorPoList = articleCategoryMapper.selectByArticleIds(articleIds);

        return articleAuthorPoList.stream().collect(Collectors.groupingBy(ArticleCategoryPo::getArticleId));
    }

    @Override
    public List<CategoryVo> selectCategory() {
        //List<String> keyList = new ArrayList<>();
        //if(StringUtils.isNotBlank(queryKeywords)) {
        //    String[] keywordArr = queryKeywords.split(","); //前端自动将关键词之间的空格使用英文半角逗号分割
        //
        //    keyList.addAll(Arrays.asList(keywordArr));
        //}
        List<ArticleCategoryPo> articleCategoryPos = articleCategoryMapper.queryCategory();

        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (ArticleCategoryPo categoryPo : articleCategoryPos) {
            CategoryVo categoryVo = BeanCopyUtils.copy(categoryPo, CategoryVo.class);
            //categoryVo.setId(IDUtils.encryptByAES(categoryPo.getCategoryId()));
            //if(null != categoryPo.getParentId()) {
            //    categoryVo.setParentId(IDUtils.encryptByAES(categoryPo.getParentId()));
            //}

            categoryVoList.add(categoryVo);
        }

        //按文章数量进行逆序排序
        categoryVoList = categoryVoList.stream().sorted(Comparator.comparing(CategoryVo::getArticleNum).reversed()).collect(Collectors.toList());

        return categoryVoList;
    }

    /**
     * 该分类下的所有文章
     */
    @Override
    public PageResponseDto<ArticleVo> selectArticleByCategory(PageRequestDto<CategoryQo> requestDto) {
        CategoryQo categoryQo = requestDto.getCondition();

        //获取分类名称
        CategoryEntity categoryEntity = articleCategoryMapper.selectCategoryByCategory(categoryQo.getCategoryCode());
        categoryQo.setCategoryName(categoryEntity.getName());

        //获取分类下的所有文章ID
        List<Long> paramArticleIds = articleCategoryMapper.selectArticleByCategory(categoryQo.getCategoryCode());

        //构建查询条件
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleEntity::getDeleted, 0)
                .eq(ArticleEntity::getReleased, 1)
                .in(ArticleEntity::getId, paramArticleIds);

        //查询条件：根据关键字搜索
        String keywords = categoryQo.getQueryKeywords();
        if(StringUtils.isNotBlank(keywords)) {
            String[] keywordArr = keywords.split(",");

            //文章搜索关键字：先搜标题、再搜URL、才搜description，尽量不要搜content
            for (String key : keywordArr) {
                if(StringUtils.isBlank(key)) {
                    continue;
                }

                queryWrapper.lambda().and(
                        ele -> ele.like(ArticleEntity::getTitle, key)
                                .or()
                                .like(ArticleEntity::getUri, key)
                                .or()
                                .like(ArticleEntity::getSummary, key)
                );
            }
        }
        queryWrapper.lambda().orderByDesc(ArticleEntity::getUpdateTime);

        Page<ArticleEntity> paramPage = PaginationUtils.PageRequest2IPage(requestDto, ArticleEntity.class);
        Page<ArticleEntity> resultPage = articleMapper.selectPage(paramPage, queryWrapper);
        List<ArticleEntity> articleEntityList = resultPage.getRecords();

        PageResponseDto<ArticleVo> articleVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, ArticleVo.class);

        if(articleEntityList == null || articleEntityList.size() < 1) {
            return articleVoPageResponseDto;
        }

        List<Long> articleIds = articleEntityList.stream().map(ArticleEntity::getId).collect(Collectors.toList());
        Map<Long, List<ArticleCategoryPo>> categoryMap = selectByArticleIds(articleIds);
        List<ArticleVo> articleVoList = articleVoPageResponseDto.getRows();

        //一个文章可能有多个分类，获取所有分类名，拼接
        for (int i = 0, len = articleEntityList.size(); i < len; i++) {
            ArticleEntity articleEntity = articleEntityList.get(i);
            Long articleId = articleEntity.getId();
            List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(articleId);
            if(articleCategoryPos != null && articleCategoryPos.size() >= 1) {
                String categories = "";
                for (ArticleCategoryPo categoryPo : articleCategoryPos) {
                    categories = String.join(",", categoryPo.getName());
                }

                articleVoList.get(i).setCategories(categories);
            }
        }

        //填充文章的URL
        for (ArticleVo articleVo : articleVoList) {
            articleVo.setUri(articlePath + articleVo.getUri());
        }

        return articleVoPageResponseDto;
    }
}




