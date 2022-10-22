package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CommentAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.CommentQo;
import com.hackyle.blog.business.service.CommentService;
import com.hackyle.blog.business.vo.CommentVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;


    /**
     * 新增文章评论
     */
    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ApiRequest<CommentAddDto> apiRequest) {
        LOGGER.info("新增文章评论-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        CommentAddDto addDto = apiRequest.getData();
        if(addDto == null || StringUtils.isBlank(addDto.getContent())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            commentService.add(addDto);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("新增文章评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除文章评论
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids")String ids) {
        LOGGER.info("删除文章评论-Controller层入参-ids={}", ids);

        if(ids == null || StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            commentService.del(ids);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());

        } catch (Exception e) {
            LOGGER.error("删除文章评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改文章评论
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<CommentAddDto> apiRequest) {
        LOGGER.info("修改文章评论-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        CommentAddDto updateDto = apiRequest.getData();
        if(updateDto == null || null == updateDto.getId()) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            commentService.update(updateDto);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());

        } catch (Exception e) {
            LOGGER.error("修改文章评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    /**
     * 分页获取层级评论（父评论中包含着子评论）
     */
    @PostMapping("/fetchListByHierarchy")
    public ApiResponse<PageResponseDto<CommentVo>> fetchListByHierarchy(@RequestBody ApiRequest<PageRequestDto<CommentQo>> apiRequest) {
        LOGGER.info("分页获取层级评论-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        PageRequestDto<CommentQo> pageRequestDto = apiRequest.getData();
        if(pageRequestDto == null) {
            //return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
            pageRequestDto = new PageRequestDto<>();
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
            PageResponseDto<CommentVo> pageResponseDto = commentService.fetchListByHierarchy(pageRequestDto);
            LOGGER.info("分页获取层级评论-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("分页获取层级评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 分页获取评论
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<CommentVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<CommentQo>> apiRequest) {
        LOGGER.info("分页获取评论-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        PageRequestDto<CommentQo> pageRequestDto = apiRequest.getData();
        if(pageRequestDto == null || pageRequestDto.getCondition() == null) {
            //return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
            pageRequestDto = new PageRequestDto<>();
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
            PageResponseDto<CommentVo> pageResponseDto = commentService.fetchList(pageRequestDto);
            LOGGER.info("分页获取评论-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("分页获取评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    /**
     * 获取评论
     */
    @PostMapping("/fetchByTargetId")
    public ApiResponse<String> fetchByTargetId(@RequestParam("targetId")String targetId) {
        LOGGER.info("获取评论-Controller层入参-targetId={}", JSON.toJSONString(targetId));

        if(targetId == null || StringUtils.isBlank(targetId)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            //boolean signUpFlag = commentService.allArticle(data);
            boolean signUpFlag = true;
            LOGGER.info("获取评论-Controller层出参-signUpFlag={}", signUpFlag);

            if(signUpFlag) {
                return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
            } else {
                return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }
}
