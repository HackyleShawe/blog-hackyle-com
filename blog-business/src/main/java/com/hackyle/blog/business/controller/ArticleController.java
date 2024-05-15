package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiRequest;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ArticleAddDto;
import com.hackyle.blog.business.qo.ArticleQo;
import com.hackyle.blog.business.service.ArticleService;
import com.hackyle.blog.business.vo.ArticleVo;
import com.hackyle.blog.business.vo.AuthorVo;
import com.hackyle.blog.business.vo.CategoryVo;
import com.hackyle.blog.business.vo.TagVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;


    /**
     * 新增文章
     */
    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ApiRequest<ArticleAddDto> apiRequest) {
        ArticleAddDto addDto = apiRequest.getData();
        LOGGER.info("新增文章-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null || StringUtils.isBlank(addDto.getTitle()) || StringUtils.isBlank(addDto.getUri())
                || StringUtils.isBlank(addDto.getContent())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return articleService.add(addDto);
        } catch (Exception e) {
            LOGGER.error("新增文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids")String ids) {
        LOGGER.info("删除文章-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return articleService.del(ids);
            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("删除文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除文章(物理删除)
     */
    @DeleteMapping("/delReal")
    public ApiResponse<String> delReal(@RequestParam("ids")String ids) {
        LOGGER.info("删除文章(物理删除)-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return articleService.delReal(ids);
        } catch (Exception e) {
            LOGGER.error("删除文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改文章
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<ArticleAddDto> apiRequest) {
        ArticleAddDto addDto = apiRequest.getData();
        LOGGER.info("修改文章-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null || null == addDto.getId()) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return articleService.update(addDto);
        } catch (Exception e) {
            LOGGER.error("修改文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 文章详情
     */
    @GetMapping("/fetch")
    public ApiResponse<ArticleVo> fetch(@RequestParam("id")String id) {
        LOGGER.info("获取文章详情-controller层入参-id={}", id);

        if(StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            ArticleVo articleVo = articleService.fetch(id);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), articleVo);
        } catch (Exception e) {
            LOGGER.error("获取文章详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取文章的作者信息
     */
    @GetMapping("/fetchAuthor")
    public ApiResponse<List<AuthorVo>> fetchAuthor(@RequestParam("articleId") String articleId) {
        LOGGER.info("获取文章的作者信息-controller层入参-articleId={}", articleId);

        if(StringUtils.isBlank(articleId)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            List<AuthorVo> authorVoList = articleService.fetchAuthor(articleId);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), authorVoList);
        } catch (Exception e) {
            LOGGER.error("获取文章的作者信息-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取文章的分类信息
     */
    @GetMapping("/fetchCategory")
    public ApiResponse<List<CategoryVo>> fetchCategory(@RequestParam("articleId") String articleId) {
        LOGGER.info("获取文章的分类信息-controller层入参-articleId={}", articleId);

        if(StringUtils.isBlank(articleId)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            List<CategoryVo> categoryVoList = articleService.fetchCategory(articleId);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), categoryVoList);
        } catch (Exception e) {
            LOGGER.error("获取文章的分类信息-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取文章的标签信息
     */
    @GetMapping("/fetchTag")
    public ApiResponse<List<TagVo>> fetchTag(@RequestParam("articleId") String articleId) {
        LOGGER.info("获取文章的标签信息-controller层入参-articleId={}", articleId);

        if(StringUtils.isBlank(articleId)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            List<TagVo> tagVoList = articleService.fetchTag(articleId);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), tagVoList);
        } catch (Exception e) {
            LOGGER.error("获取文章的标签信息-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 分页获取所有文章
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<ArticleVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<ArticleQo>> apiRequest) {
        PageRequestDto<ArticleQo> pageRequestDto = apiRequest.getData();
        LOGGER.info("获取所有文章-Controller层入参-pageRequestDto={}", JSON.toJSONString(pageRequestDto));

        if(pageRequestDto == null) {
            //return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
            pageRequestDto = new PageRequestDto<ArticleQo>();
            pageRequestDto.setCurrentPage(1);
            pageRequestDto.setPageSize(10);
        }

        //纠正不合法数据
        if(pageRequestDto.getPageSize() < 1) {
            pageRequestDto.setPageSize(10);
        }
        if(pageRequestDto.getCurrentPage() < 1) {
            pageRequestDto.setCurrentPage(1);
        }

        try {
            PageResponseDto<ArticleVo> pageResponseDto = articleService.fetchList(pageRequestDto);

            //文章的Content打日志太大了，使用URI替换
            String articleUris = pageResponseDto.getRows().stream().map(ArticleVo::getUri).collect(Collectors.joining(","));
            LOGGER.info("获取所有文章-Controller层出参-articleUris={}", articleUris);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("获取所有文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
