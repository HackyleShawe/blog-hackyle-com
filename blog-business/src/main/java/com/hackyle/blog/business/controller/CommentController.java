package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiRequest;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CommentAddDto;
import com.hackyle.blog.business.qo.CommentQo;
import com.hackyle.blog.business.service.CommentService;
import com.hackyle.blog.business.vo.CommentVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
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
        CommentAddDto commentAddDto = apiRequest.getData();
        LOGGER.info("新增文章评论-Controller层入参-commentAddDto={}", JSON.toJSONString(commentAddDto));
        if(commentAddDto == null || StringUtils.isBlank(commentAddDto.getName()) || StringUtils.isBlank(commentAddDto.getEmail())
                || StringUtils.isBlank(commentAddDto.getContent())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return commentService.add(commentAddDto);
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

        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return commentService.del(ids);
        } catch (Exception e) {
            LOGGER.error("删除文章评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除文章评论(物理删除)
     */
    @DeleteMapping("/delReal")
    public ApiResponse<String> delReal(@RequestParam("ids")String ids) {
        LOGGER.info("删除文章评论(物理删除)-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return commentService.delReal(ids);
        } catch (Exception e) {
            LOGGER.error("删除文章评论(物理删除)-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改文章评论
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<CommentAddDto> apiRequest) {
        CommentAddDto updateDto = apiRequest.getData();
        LOGGER.info("修改文章评论-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));
        if(updateDto == null || null == updateDto.getId()) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return commentService.update(updateDto);

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
        PageRequestDto<CommentQo> pageRequestDto = apiRequest.getData();
        LOGGER.info("分页获取层级评论-Controller层入参-pageRequestDto={}", JSON.toJSONString(pageRequestDto));

        if(pageRequestDto == null) {
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

}
