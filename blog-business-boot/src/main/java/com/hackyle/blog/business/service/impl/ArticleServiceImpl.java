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
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.ArticleEntity;
import com.hackyle.blog.business.entity.CategoryEntity;
import com.hackyle.blog.business.mapper.ArticleMapper;
import com.hackyle.blog.business.mapper.CategoryMapper;
import com.hackyle.blog.business.po.ArticleAuthorPo;
import com.hackyle.blog.business.po.ArticleCategoryPo;
import com.hackyle.blog.business.po.ArticleTagPo;
import com.hackyle.blog.business.qo.ArticleQo;
import com.hackyle.blog.business.service.*;
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
import java.util.Arrays;
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
    private CategoryMapper categoryMapper;

    @Autowired
    private CommentService commentService;

    @Transactional
    @Override
    public ApiResponse<String> add(ArticleAddDto articleAddDto) throws Exception {
        checkAndAdjustURI(articleAddDto);

        ArticleEntity articleEntity = BeanCopyUtils.copy(articleAddDto, ArticleEntity.class);

        //检查一下文章是否插入：检查URI是否已经存在
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uri", articleEntity.getUri());
        ArticleEntity checkArticleEntity = articleMapper.selectOne(queryWrapper);

        if(checkArticleEntity != null) {
            //文章已存在，执行更新操作
            articleAddDto.setId(IDUtils.encryptByAES(checkArticleEntity.getId()));
            return update(articleAddDto);

        } else {
            //文章不存在，执行新增操作
            articleEntity.setId(IDUtils.timestampID());
            int inserted = articleMapper.insert(articleEntity);
            if(inserted != 1) {
                throw new RuntimeException("新增文章失败");
            }

            if(StringUtils.isNotBlank(articleAddDto.getAuthorIds())) {
                List<String> authorIdList = Arrays.asList(articleAddDto.getAuthorIds().split(","));
                List<Long> authorIds = IDUtils.decrypt(authorIdList);
                articleAuthorService.batchInsert(articleEntity.getId(), authorIds);
            }
            if(StringUtils.isNotBlank(articleAddDto.getCategoryIds())) {
                List<String> categoryIdList = Arrays.asList(articleAddDto.getCategoryIds().split(","));
                List<Long> categoryIds = IDUtils.decrypt(categoryIdList);
                articleCategoryService.batchInsert(articleEntity.getId(), categoryIds);
            }
            if(StringUtils.isNotBlank(articleAddDto.getTagIds())) {
                List<Long> tagIds = IDUtils.decrypt(Arrays.asList(articleAddDto.getTagIds().split(",")));
                articleTagService.batchInsert(articleEntity.getId(), tagIds);
            }
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Transactional
    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(IDUtils.decryptByAES(idStr));
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
            articleIds.add(IDUtils.decryptByAES(idStr));
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
        adjustURI(articleUpdateDto);

        ArticleEntity articleEntity = BeanCopyUtils.copy(articleUpdateDto, ArticleEntity.class);
        IDUtils.decrypt(articleUpdateDto, articleEntity);

        UpdateWrapper<ArticleEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ArticleEntity::getId, articleEntity.getId());
        int update = articleMapper.update(articleEntity, updateWrapper);
        if(update != 1) {
            throw new RuntimeException("文章更新失败");
        }

        long articleId = articleEntity.getId();

        if(StringUtils.isNotBlank(articleUpdateDto.getAuthorIds())) {
            List<Long> authorIds = IDUtils.decrypt(Arrays.asList(articleUpdateDto.getAuthorIds().split(",")));
            articleAuthorService.update(articleId, authorIds);
        }
        if(StringUtils.isNotBlank(articleUpdateDto.getCategoryIds())) {
            List<Long> categoryIds = IDUtils.decrypt(Arrays.asList(articleUpdateDto.getCategoryIds().split(",")));
            articleCategoryService.update(articleId, categoryIds);
        }
        if(StringUtils.isNotBlank(articleUpdateDto.getTagIds())) {
            List<Long> tagIds = IDUtils.decrypt(Arrays.asList(articleUpdateDto.getTagIds().split(",")));
            articleTagService.update(articleId, tagIds);
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ArticleVo fetch(String id) {
        long idd = IDUtils.decryptByAES(id);

        ArticleEntity articleEntity = articleMapper.selectById(idd);

        ArticleVo articleEntityLog = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
        articleEntityLog.setContent("文章内容长度："+articleEntityLog.getContent().length());
        LOGGER.info("获取文章-入参-idd={}-数据库查询结果-article={}", idd, JSON.toJSONString(articleEntityLog));

        ArticleVo articleVo = BeanCopyUtils.copy(articleEntity, ArticleVo.class);
        articleVo.setId(IDUtils.encryptByAES(articleEntity.getId()));

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleAuthorPo>> authorMap = articleAuthorService.selectByArticleIds(articleIds);
        List<ArticleAuthorPo> articleAuthorPos = authorMap.get(idd);
        List<AuthorVo> authorVos = BeanCopyUtils.copyList(articleAuthorPos, AuthorVo.class);
        IDUtils.batchEncrypt(articleAuthorPos, "authorId", authorVos, "setId");
        articleVo.setAuthors(authorVos);

        Map<Long, List<ArticleCategoryPo>> categoryMap = articleCategoryService.selectByArticleIds(articleIds);
        List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(idd);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyList(articleCategoryPos, CategoryVo.class);
        IDUtils.batchEncrypt(articleCategoryPos, "categoryId", categoryVos, "setId");
        articleVo.setCategories(categoryVos);

        Map<Long, List<ArticleTagPo>> tagMap = articleTagService.selectByArticleIds(articleIds);
        List<ArticleTagPo> articleTagPos = tagMap.get(idd);
        List<TagVo> tagVos = BeanCopyUtils.copyList(articleTagPos, TagVo.class);
        IDUtils.batchEncrypt(articleTagPos, "tagId", tagVos, "setId");
        articleVo.setTags(tagVos);

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

        //文章的Content打日志太大了，使用URI替换
        String articleUris = resultPage.getRecords().stream().map(ArticleEntity::getUri).collect(Collectors.joining(","));
        LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-articleUris={}", JSON.toJSONString(pageRequestDto), articleUris);

        PageResponseDto<ArticleVo> articleVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, ArticleVo.class);
        List<ArticleEntity> articleEntityList = resultPage.getRecords();
        IDUtils.batchEncrypt(articleEntityList, articleVoPageResponseDto.getRows());
        List<Long> articleIdList = articleEntityList.stream().map(ArticleEntity::getId).collect(Collectors.toList());
        if(articleIdList.size() < 1) {
            return articleVoPageResponseDto;
        }

        Map<Long, List<ArticleAuthorPo>> authorMap = articleAuthorService.selectByArticleIds(articleIdList);
        Map<Long, List<ArticleCategoryPo>> categoryMap = articleCategoryService.selectByArticleIds(articleIdList);
        Map<Long, List<ArticleTagPo>> tagMap = articleTagService.selectByArticleIds(articleIdList);

        List<ArticleVo> articleVoList = articleVoPageResponseDto.getRows();
        for (ArticleVo articleVo : articleVoList) {
            long idd = IDUtils.decryptByAES(articleVo.getId());

            List<ArticleAuthorPo> articleAuthorPos = authorMap.get(idd);
            List<AuthorVo> authorVos = BeanCopyUtils.copyList(articleAuthorPos, AuthorVo.class);
            IDUtils.batchEncrypt(articleAuthorPos, "authorId", authorVos, "setId");
            articleVo.setAuthors(authorVos);

            List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(idd);
            List<CategoryVo> categoryVos = BeanCopyUtils.copyList(articleCategoryPos, CategoryVo.class);
            IDUtils.batchEncrypt(articleCategoryPos, "categoryId", categoryVos, "setId");
            articleVo.setCategories(categoryVos);

            List<ArticleTagPo> articleTagPos = tagMap.get(idd);
            List<TagVo> tagVos = BeanCopyUtils.copyList(articleTagPos, TagVo.class);
            IDUtils.batchEncrypt(articleTagPos, "tagId", tagVos, "setId");
            articleVo.setTags(tagVos);
        }
        articleVoPageResponseDto.setRows(articleVoList);

        return articleVoPageResponseDto;
    }

    @Override
    public List<AuthorVo> fetchAuthor(String articleId) {
        long idd = IDUtils.decryptByAES(articleId);

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleAuthorPo>> articleMap = articleAuthorService.selectByArticleIds(articleIds);

        List<ArticleAuthorPo> articleAuthorPos = articleMap.get(idd);
        List<AuthorVo> authorVos = BeanCopyUtils.copyList(articleAuthorPos, AuthorVo.class);
        IDUtils.batchEncrypt(articleAuthorPos, "authorId", authorVos, "setId");

        return authorVos;
    }

    @Override
    public List<CategoryVo> fetchCategory(String articleId) {
        long idd = IDUtils.decryptByAES(articleId);

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleCategoryPo>> categoryMap = articleCategoryService.selectByArticleIds(articleIds);
        List<ArticleCategoryPo> articleCategoryPos = categoryMap.get(idd);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyList(articleCategoryPos, CategoryVo.class);
        IDUtils.batchEncrypt(articleCategoryPos, "categoryId", categoryVos, "setId");

        return categoryVos;
    }

    @Override
    public List<TagVo> fetchTag(String articleId) {
        long idd = IDUtils.decryptByAES(articleId);

        List<Long> articleIds = new ArrayList<>();
        articleIds.add(idd);

        Map<Long, List<ArticleTagPo>> tagMap = articleTagService.selectByArticleIds(articleIds);
        List<ArticleTagPo> articleTagPos = tagMap.get(idd);
        List<TagVo> tagVos = BeanCopyUtils.copyList(articleTagPos, TagVo.class);
        IDUtils.batchEncrypt(articleTagPos, "tagId", tagVos, "setId");

        return tagVos;
    }

    /**
     * 调整URI的格式
     * 1.全部小写
     * 2.只能含有的特殊字符：下换线，分割线
     * 3.如果有分类，则在URI前面加上分类Code
     */
    private void checkAndAdjustURI(ArticleAddDto articleAddDto) {
        String uri = articleAddDto.getUri();

        if(StringUtils.isBlank(uri)) {
            return;
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

        //在URI前拼接文章分类的编码
        String categoryIds = articleAddDto.getCategoryIds();
        if(StringUtils.isNotBlank(categoryIds) && categoryIds.split(",").length >= 1) {
            String[] categoryIdArr = categoryIds.split(",");

            long decryptedCategoryId = IDUtils.decryptByAES(categoryIdArr[0]);
            CategoryEntity categoryEntity = categoryMapper.selectById(decryptedCategoryId);
            if(categoryEntity != null && StringUtils.isNotBlank(categoryEntity.getCode())) {
                uri = "/" + categoryEntity.getCode().toLowerCase() + uri;
            }
        }

        articleAddDto.setUri(uri);
    }

    /**
     * 调整URI的格式
     * 1.全部小写
     * 2.只能含有的特殊字符：下换线，分割线
     */
    private void adjustURI(ArticleAddDto articleAddDto) {
        if(StringUtils.isBlank(articleAddDto.getUri())) {
            return;
        }

        String uri = articleAddDto.getUri();
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
