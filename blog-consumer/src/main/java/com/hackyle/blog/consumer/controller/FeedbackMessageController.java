package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.consumer.common.constant.ResponseEnum;
import com.hackyle.blog.consumer.common.pojo.ApiRequest;
import com.hackyle.blog.consumer.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.FeedbackMessageAddDto;
import com.hackyle.blog.consumer.dto.PageRequestDto;
import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.service.FeedbackMessageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbackMessage")
public class FeedbackMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackMessageController.class);

    @Autowired
    private FeedbackMessageService feedbackMessageService;

    /**
     * 新增留言反馈
     */
    @PostMapping("/add")
    public ApiResponse<String> add(FeedbackMessageAddDto addDto) {
        LOGGER.info("新增留言反馈-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return feedbackMessageService.add(addDto);
        } catch (Exception e) {
            LOGGER.error("新增留言反馈-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
