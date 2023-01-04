package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.consumer.dto.PageRequestDto;
import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.entity.ArticleEntity;
import com.hackyle.blog.consumer.entity.ArticleTagEntity;
import com.hackyle.blog.consumer.entity.TagEntity;
import com.hackyle.blog.consumer.mapper.ArticleMapper;
import com.hackyle.blog.consumer.mapper.ArticleTagMapper;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;
import com.hackyle.blog.consumer.po.ArticleTagPo;
import com.hackyle.blog.consumer.qo.TagQo;
import com.hackyle.blog.consumer.service.ArticleCategoryService;
import com.hackyle.blog.consumer.service.ArticleTagService;
import com.hackyle.blog.consumer.util.BeanCopyUtils;
import com.hackyle.blog.consumer.util.PaginationUtils;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.consumer.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTagEntity>
    implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Override
    public Map<Long, List<ArticleTagPo>> selectByArticleIds(List<Long> articleIds) {
        List<ArticleTagPo> articleAuthorPoList = articleTagMapper.selectByArticleIds(articleIds);

        return articleAuthorPoList.stream().collect(Collectors.groupingBy(ArticleTagPo::getArticleId));
    }

    @Override
    public List<TagVo> selectTag() {
        List<ArticleTagPo> articleTagPos = articleTagMapper.selectTag();
        List<TagVo> tagVos = BeanCopyUtils.copyList(articleTagPos, TagVo.class);
        return tagVos.stream().sorted(Comparator.comparing(TagVo::getArticleNum).reversed()).collect(Collectors.toList());
    }

    @Override
    public PageResponseDto<ArticleVo> selectArticleByTag(PageRequestDto<TagQo> requestDto) {
        TagQo tagQo = requestDto.getCondition();

        TagEntity tagEntity = articleTagMapper.selectTagByTagCode(tagQo.getTagCode());
        tagQo.setTagName(tagEntity.getName());

        List<Long> paramArticleIds = articleTagMapper.selectArticleByTag(tagQo.getTagCode());

        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ArticleEntity::getDeleted, 0)
                .eq(ArticleEntity::getReleased, 1)
                .in(ArticleEntity::getId, paramArticleIds);

        String tagKeys = tagQo.getTagKeys();
        if(StringUtils.isNotBlank(tagKeys)) {
            String[] keywordArr = tagKeys.split(",");

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

        return articleVoPageResponseDto;
    }
}




