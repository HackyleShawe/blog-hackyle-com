package com.hackyle.blog.business.controller;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiRequest;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.qo.ArticleAccessLogQo;
import com.hackyle.blog.business.service.LoggerService;
import com.hackyle.blog.business.vo.ArticleAccessLogVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logger")
public class LoggerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerController.class);

    @Autowired
    private LoggerService loggerService;

    @PostMapping("/articleAccess")
    public ApiResponse<PageResponseDto<ArticleAccessLogVo>> articleAccess(@RequestBody ApiRequest<PageRequestDto<ArticleAccessLogQo>> apiRequest) {
        PageRequestDto<ArticleAccessLogQo> pageRequest = apiRequest.getData();
        LOGGER.info("获取文章访问日志-controller入参-pageRequest={}", JSON.toJSONString(pageRequest));

        PageResponseDto<ArticleAccessLogVo> pageResponse = loggerService.articleAccess(pageRequest);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponse);
    }
}
