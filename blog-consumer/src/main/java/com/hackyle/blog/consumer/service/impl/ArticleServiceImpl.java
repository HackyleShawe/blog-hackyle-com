package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.common.constant.ConfigItemEnum;
import com.hackyle.blog.consumer.entity.ArticleEntity;
import com.hackyle.blog.consumer.entity.ConfigurationEntity;
import com.hackyle.blog.consumer.mapper.ArticleMapper;
import com.hackyle.blog.consumer.po.ArticleAuthorPo;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;
import com.hackyle.blog.consumer.po.ArticleTagPo;
import com.hackyle.blog.consumer.service.*;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.IDUtils;
import com.hackyle.blog.common.util.PaginationUtils;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
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
    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ValueOperations<String, String> redisValueOperations;

    @Value("${blog.article-path}")
    private String articlePath;

    @Override
    public PageResponseDto<ArticleVo> pageByNum(PageRequestDto<String> pageRequestDto) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleEntity::getDeleted, 0)
                .eq(ArticleEntity::getReleased, 1);

        String keywords = pageRequestDto.getCondition();
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

        Page<ArticleEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, ArticleEntity.class);
        Page<ArticleEntity> resultPage = articleMapper.selectPage(paramPage, queryWrapper);

        //如果是第一页则塞入置顶文章
        if(pageRequestDto.getCurrentPage() == 1) {
            List<ArticleEntity> articleRecords = resultPage.getRecords();
            //List<ArticleVo> articleRows = articleVoPageResponseDto.getRows();
            List<ArticleEntity> topArticles = topArticleByIndexPage();
            if(topArticles != null && topArticles.size() > 0) {
                //过滤掉置顶已经出现过的文章
                List<Long> topArticleIds = topArticles.stream().map(ArticleEntity::getId).collect(Collectors.toList());
                articleRecords.removeIf(article -> topArticleIds.contains(article.getId()));

                List<ArticleEntity> resArticleVos = new ArrayList<>();
                resArticleVos.addAll(topArticles);
                resArticleVos.addAll(articleRecords);
                resultPage.setRecords(resArticleVos);
            }
        }
        PageResponseDto<ArticleVo> articleVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, ArticleVo.class);
        List<ArticleEntity> articleEntityList = resultPage.getRecords();
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

    @Override
    public ArticleVo articleDetail(String uri) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleEntity::getDeleted, 0)
                    .eq(ArticleEntity::getReleased, 1)
                    .eq(ArticleEntity::getUri, uri);
        ArticleEntity articleEntity = articleMapper.selectOne(queryWrapper);

        ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
        //加密ID
        articleVo.setId(IDUtils.encrypt(articleEntity.getId()));

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

        //填充文章的URL
        articleVo.setUri(articlePath + articleVo.getUri());

        return articleVo;
    }

    /**
     * 获取置顶到首页的文章
     */
    private List<ArticleEntity> topArticleByIndexPage() {
        //先从缓存中取
        String redisKey = ConfigItemEnum.ARTICLE_TOP.getGroup() + "::" + ConfigItemEnum.ARTICLE_TOP.getKey();
        String redisVal = redisValueOperations.get(redisKey);
        LOGGER.info("从缓存中获取需要置顶的文章ID={}", redisVal);
        if(StringUtils.isNotBlank(redisVal)) {
            List<Long> topArticleIds = new ArrayList<>();
            for (String aid : redisVal.split(",")) {
                if(StringUtils.isBlank(aid)) {
                    continue;
                }
                topArticleIds.add(Long.parseLong(aid));
            }
            return topArticleIds.isEmpty() ? null : articleMapper.selectBatchIds(topArticleIds);
        }

        //再查库
        ConfigurationEntity configurationEntity = configurationService.queryConfigByKey(ConfigItemEnum.ARTICLE_TOP);
        if(configurationEntity == null) { //库中没有相关配置，直接返回
            return null;
        }
        String configValue = configurationEntity.getConfigValue();
        LOGGER.info("查库获取需要置顶的文章ID={}", configValue);
        if(StringUtils.isNotBlank(configValue)) {
            List<Long> topArticleIds = new ArrayList<>();
            for (String aid : configValue.split(",")) {
                if(StringUtils.isBlank(aid)) {
                    continue;
                }
                topArticleIds.add(Long.parseLong(aid));
            }
            return topArticleIds.isEmpty() ? null : articleMapper.selectBatchIds(topArticleIds);
        }

        return null;
    }

}
