package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.consumer.common.constant.ResponseEnum;
import com.hackyle.blog.consumer.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.CommentAddDto;
import com.hackyle.blog.consumer.service.CommentService;
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
    public ApiResponse<String> add(CommentAddDto commentAddDto) {
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

}
