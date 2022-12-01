package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.consumer.dto.PageRequestDto;
import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.entity.ArticleEntity;
import com.hackyle.blog.consumer.mapper.ArticleMapper;
import com.hackyle.blog.consumer.po.ArticleAuthorPo;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;
import com.hackyle.blog.consumer.po.ArticleTagPo;
import com.hackyle.blog.consumer.qo.ArticleQo;
import com.hackyle.blog.consumer.service.ArticleAuthorService;
import com.hackyle.blog.consumer.service.ArticleCategoryService;
import com.hackyle.blog.consumer.service.ArticleService;
import com.hackyle.blog.consumer.service.ArticleTagService;
import com.hackyle.blog.consumer.util.BeanCopyUtils;
import com.hackyle.blog.consumer.util.IDUtils;
import com.hackyle.blog.consumer.util.PaginationUtils;
import com.hackyle.blog.consumer.vo.ArticleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ArticleServiceImpl implements ArticleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCategoryService articleCategoryService;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private ArticleAuthorService articleAuthorService;

    @Override
    public PageResponseDto<ArticleVo> pageByNum(Integer pageNum) {
        PageRequestDto<ArticleQo> pageRequestDto = new PageRequestDto<>();
        pageRequestDto.setCurrentPage(pageNum);
        pageRequestDto.setPageSize(15);

        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleEntity::getDeleted, 0)
                .eq(ArticleEntity::getReleased, 1);

        Page<ArticleEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, ArticleEntity.class);
        Page<ArticleEntity> resultPage = articleMapper.selectPage(paramPage, queryWrapper);
        List<ArticleEntity> articleEntityList = resultPage.getRecords();

        PageResponseDto<ArticleVo> articleVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, ArticleVo.class);

        if(articleEntityList == null || articleEntityList.size() < 1) {
            return articleVoPageResponseDto;
        }

        List<Long> articleIds = articleEntityList.stream().map(ArticleEntity::getId).collect(Collectors.toList());
        Map<Long, List<ArticleCategoryPo>> categoryMap = articleCategoryService.selectByArticleIds(articleIds);
        List<ArticleVo> articleVoList = articleVoPageResponseDto.getRows();

        for (int i = 0, len = articleEntityList.size(); i < len; i++) {
            ArticleEntity articleEntity = articleEntityList.get(i);
            Long articleId = articleEntity.getId();
            List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(articleId);
            if(articleCategoryPos != null && articleCategoryPos.size() > 1) {
                String categories = "";
                for (ArticleCategoryPo categoryPo : articleCategoryPos) {
                    categories = String.join(",", categoryPo.getName());
                }

                articleVoList.get(i).setCategories(categories);
            }
        }

        //对文章按照更新时间进行逆序排序
        articleVoList = articleVoList.stream().sorted(Comparator.comparing(ArticleVo::getUpdateTime).reversed()).collect(Collectors.toList());
        articleVoPageResponseDto.setRows(articleVoList);

        return articleVoPageResponseDto;
    }

    @Override
    public ArticleVo articleDetail(String uri) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleEntity::getDeleted, 0)
                    .eq(ArticleEntity::getReleased, 1)
                    .eq(ArticleEntity::getUri, uri);
        ArticleEntity articleEntity = articleMapper.selectOne(queryWrapper);

        ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
        //加密ID
        articleVo.setId(IDUtils.encryptByAES(articleEntity.getId()));

        Long articleId = articleEntity.getId();
        List<Long> articleIds = new ArrayList<>();
        articleIds.add(articleId);

        Map<Long, List<ArticleCategoryPo>> articleCategoryMap = articleCategoryService.selectByArticleIds(articleIds);
        if(articleCategoryMap != null && articleCategoryMap.containsKey(articleId)) {
            List<ArticleCategoryPo> articleCategoryPoList = articleCategoryMap.get(articleId);
            String categories = articleCategoryPoList.stream().map(ArticleCategoryPo::getName).collect(Collectors.joining(", "));;
            articleVo.setCategories(categories);
        }


        Map<Long, List<ArticleTagPo>> tagMap = articleTagService.selectByArticleIds(articleIds);
        if(tagMap != null && tagMap.containsKey(articleId)) {
            List<ArticleTagPo> articleTagPoList = tagMap.get(articleId);
            String tags = articleTagPoList.stream().map(ArticleTagPo::getName).collect(Collectors.joining(", "));;
            articleVo.setTags(tags);
        }

        Map<Long, List<ArticleAuthorPo>> authorMap = articleAuthorService.selectByArticleIds(articleIds);
        if(authorMap != null && authorMap.containsKey(articleId)) {
            List<ArticleAuthorPo> articleAuthorPoList = authorMap.get(articleId);
            String authors = articleAuthorPoList.stream().map(ArticleAuthorPo::getNickName).collect(Collectors.joining(", "));;
            articleVo.setAuthors(authors);
        }

        return articleVo;
    }

}
