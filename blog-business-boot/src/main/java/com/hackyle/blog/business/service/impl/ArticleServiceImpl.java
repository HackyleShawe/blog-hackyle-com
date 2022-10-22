package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.common.constant.RegexVerificationCons;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ArticleAddDto;
import com.hackyle.blog.business.qo.ArticleQo;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.mapper.ArticleMapper;
import com.hackyle.blog.business.service.ArticleAuthorService;
import com.hackyle.blog.business.service.ArticleCategoryService;
import com.hackyle.blog.business.service.ArticleService;
import com.hackyle.blog.business.service.ArticleTagService;
import com.hackyle.blog.business.service.CommentService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.ArticleVo;
import com.hackyle.blog.business.vo.AuthorVo;
import com.hackyle.blog.business.vo.CategoryVo;
import com.hackyle.blog.business.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Transactional
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity>
        implements ArticleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);
    private static final Pattern patternRUI = Pattern.compile(RegexVerificationCons.URI);


    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleAuthorService articleAuthorService;
    @Autowired
    private ArticleCategoryService articleCategoryService;
    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private CommentService commentService;

    @Transactional
    @Override
    public ApiResponse<String> add(ArticleAddDto articleAddDto) throws Exception {
        adjustUri(articleAddDto);

        ArticleEntity articleEntity = BeanCopyUtils.copy(articleAddDto, ArticleEntity.class);

        //检查一下文章是否插入：检查URI是否已经存在
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uri", articleEntity.getUri());
        ArticleEntity checkArticleEntity = articleMapper.selectOne(queryWrapper);

        if(checkArticleEntity != null) {
            //文章已存在，执行更新操作
            articleAddDto.setId(checkArticleEntity.getId());
            return update(articleAddDto);

        } else {
            //文章不存在，执行新增操作
            articleEntity.setId(IDUtils.timestampID());
            int inserted = articleMapper.insert(articleEntity);
            if(inserted != 1) {
                throw new RuntimeException("新增文章失败");
            }

            articleAuthorService.batchInsert(articleEntity.getId(), articleAddDto.getAuthorIds());
            articleCategoryService.batchInsert(articleEntity.getId(), articleAddDto.getCategoryIds());
            articleTagService.batchInsert(articleEntity.getId(), articleAddDto.getTagIds());
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Transactional
    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(Long.parseLong(idStr));
        }
        articleMapper.logicDeleteByIds(idList);

        //逻辑删除文章的时候，不需要逻辑删除文章的作者、目录、标签、评论等信息
        //articleAuthorService.logicDelByArticleIds(idList);
        //articleCategoryService.logicDelByArticleIds(idList);
        //articleTagService.logicDelByArticleIds(idList);
        //commentService.delByTargetIds(idList);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Transactional
    @Override
    public ApiResponse<String> delReal(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> articleIds = new ArrayList<>();
        for (String idStr : idSplit) {
            articleIds.add(Long.parseLong(idStr));
        }

        this.removeByIds(articleIds);

        //物理删除时，需要逻辑删除文章的作者、目录、标签、评论等信息
        articleAuthorService.batchDelByArticleIds(articleIds);
        articleCategoryService.batchDelByArticleIds(articleIds);
        articleTagService.batchDelByArticleIds(articleIds);
        //commentService.delByTargetIds(idList);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Transactional
    @Override
    public ApiResponse<String> update(ArticleAddDto articleUpdateDto) {
        adjustUri(articleUpdateDto);

        ArticleEntity articleEntity = BeanCopyUtils.copy(articleUpdateDto, ArticleEntity.class);

        Boolean newVersion = articleUpdateDto.getNewVersion();
        if(null != newVersion && newVersion) { //存为新版本
            //获取旧版本的编号
            ArticleEntity selectArticle= articleMapper.selectById(articleUpdateDto.getId());
            Integer version = selectArticle.getVersion();
            version ++;

            //TODO 版本号对于URI重复检查、查询操作等
            ArticleEntity newVersionArticle = BeanCopyUtils.copy(articleUpdateDto, ArticleEntity.class);
            newVersionArticle.setId(IDUtils.timestampID());
            newVersionArticle.setVersion(version);
            int insert = articleMapper.insert(newVersionArticle);
            if(insert < 1) {
                throw new RuntimeException("文章更新失败！");
            }

            articleAuthorService.batchInsert(newVersionArticle.getId(), articleUpdateDto.getAuthorIds());
            articleCategoryService.batchInsert(newVersionArticle.getId(), articleUpdateDto.getCategoryIds());
            articleTagService.batchInsert(newVersionArticle.getId(), articleUpdateDto.getTagIds());

        } else {
            UpdateWrapper<ArticleEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(ArticleEntity::getId, articleUpdateDto.getId());
            int update = articleMapper.update(articleEntity, updateWrapper);
            if(update != 1) {
                throw new RuntimeException("文章更新失败");
            }

            long articleId = articleEntity.getId();
            articleAuthorService.batchUpdate(articleId, articleUpdateDto.getAuthorIds());
            articleCategoryService.batchUpdate(articleId, articleUpdateDto.getCategoryIds());
            articleTagService.batchUpdate(articleId, articleUpdateDto.getTagIds());
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ArticleVo fetch(long idd) {
        ArticleEntity articleEntity = articleMapper.selectById(idd);
        LOGGER.info("获取文章-入参-idd={}-数据库查询结果-article={}", idd, JSON.toJSONString(articleEntity));

        ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<AuthorVo>> authorMap = articleAuthorService.selectByArticleIds(articleIds);
        articleVo.setAuthors(authorMap.get(idd));

        Map<Long, List<CategoryVo>> categoryMap = articleCategoryService.selectByArticleIds(articleIds);
        articleVo.setCategories(categoryMap.get(idd));

        Map<Long, List<TagVo>> tagMap = articleTagService.selectByArticleIds(articleIds);
        articleVo.setTags(tagMap.get(idd));

        return articleVo;
    }

    /**
     * 分页获取所有文章
     * @param pageRequestDto 查询条件
     * @return 文章
     */
    @Override
    public PageResponseDto<ArticleVo> fetchList(PageRequestDto<ArticleQo> pageRequestDto) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply(" 1 = 1 ");

        //组装查询条件
        ArticleQo articleQo = pageRequestDto.getCondition();
        if(articleQo != null) {
            if(null != articleQo.getReleased()) {
                queryWrapper.eq("is_released", articleQo.getReleased());
            }

            boolean deleted = articleQo.getDeleted() != null && articleQo.getDeleted();
            queryWrapper.eq("is_deleted", deleted);

            if(StringUtils.isNotBlank(articleQo.getTitle())) {
                queryWrapper.lambda().like(ArticleEntity::getTitle, articleQo.getTitle());
            }
            if(StringUtils.isNotBlank(articleQo.getSummary())) {
                queryWrapper.lambda().like(ArticleEntity::getSummary, articleQo.getSummary());
            }
        }

        //分页查询操作
        Page<ArticleEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, ArticleEntity.class);
        Page<ArticleEntity> resultPage = articleMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        PageResponseDto<ArticleVo> articleVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, ArticleVo.class);
        List<ArticleEntity> articleEntityList = resultPage.getRecords();
        List<Long> articleIdList = articleEntityList.stream().map(ArticleEntity::getId).collect(Collectors.toList());
        if(articleIdList.size() < 1) {
            return articleVoPageResponseDto;
        }

        Map<Long, List<AuthorVo>> authorMap = articleAuthorService.selectByArticleIds(articleIdList);
        Map<Long, List<CategoryVo>> categoryMap = articleCategoryService.selectByArticleIds(articleIdList);
        Map<Long, List<TagVo>> tagMap = articleTagService.selectByArticleIds(articleIdList);

        List<ArticleVo> articleVoList = articleVoPageResponseDto.getRows();
        for (ArticleVo articleVo : articleVoList) {
            articleVo.setAuthors(authorMap.get(articleVo.getId()));
            articleVo.setCategories(categoryMap.get(articleVo.getId()));
            articleVo.setTags(tagMap.get(articleVo.getId()));
        }
        articleVoPageResponseDto.setRows(articleVoList);

        return articleVoPageResponseDto;
    }

    /**
     * 调整URI的格式
     * 1.全部小写
     * 2.只能含有的特殊字符：下换线，分割线
     */
    private void adjustUri(ArticleAddDto articleAddDto) {
        String uri = articleAddDto.getUri();

        if(StringUtils.isBlank(uri)) {
            throw new RuntimeException("URI为空");
        }

        uri = uri.toLowerCase();

        //不合法字符检查
        Matcher matcher = patternRUI.matcher(uri);
        if(!matcher.find()) {
            throw new RuntimeException("URI不合法");
        }

        if(!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        articleAddDto.setUri(uri);
    }

}