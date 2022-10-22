package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ArticleAddDto;
import com.hackyle.blog.business.qo.ArticleQo;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.service.ArticleService;
import com.hackyle.blog.business.vo.ArticleVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

        if(addDto == null || StringUtils.isBlank(addDto.getTitle()) || StringUtils.isBlank(addDto.getUri()) || StringUtils.isBlank(addDto.getContent())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return articleService.add(addDto);
            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("新增文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(HttpServletRequest request) {
        String ids = (String) request.getAttribute("ids");
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
    public ApiResponse<String> delReal(HttpServletRequest request) {
        String ids = (String) request.getAttribute("ids");
        LOGGER.info("删除文章(物理删除)-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return articleService.delReal(ids);
            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
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
        LOGGER.info("修改文章-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        ArticleAddDto addDto = apiRequest.getData();
        if(addDto == null || null == addDto.getId()) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return articleService.update(addDto);
            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("修改文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 文章详情
     *
     */
    @GetMapping("/fetch")
    public ApiResponse<ArticleVo> fetch(HttpServletRequest request) {
        String id = String.valueOf(request.getAttribute("id"));
        LOGGER.info("获取文章详情-controller层入参-id={}", id);

        if(StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            long idd = Long.parseLong(id);
            ArticleVo articleVo = articleService.fetch(idd);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), articleVo);
        } catch (Exception e) {
            LOGGER.error("获取文章详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    /**
     * 分页获取所有文章
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<ArticleVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<ArticleQo>> apiRequest) {
        LOGGER.info("获取所有文章-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        PageRequestDto<ArticleQo> pageRequestDto = apiRequest.getData();
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
            LOGGER.info("获取所有文章-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("获取所有文章-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
