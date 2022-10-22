package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.service.FriendLinkService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friendLink")
public class FriendLinkController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendLinkController.class);

    @Autowired
    private FriendLinkService friendLinkService;


    /**
     * 新增友链
     */
    @PostMapping("/add")
    public ApiResponse<String> add(ApiRequest<String> apiRequest) {
        LOGGER.info("新增友链-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        String data = apiRequest.getData();
        if(data == null || StringUtils.isBlank(data)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            //friendLinkService.add(data);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("新增友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除友链
     */
    @PostMapping("/del")
    public ApiResponse<String> del(ApiRequest<String> apiRequest) {
        LOGGER.info("删除友链-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        String data = apiRequest.getData();
        if(data == null || StringUtils.isBlank(data)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            //friendLinkService.del(data);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("删除友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改友链
     */
    @PostMapping("/update")
    public ApiResponse<String> update(ApiRequest<String> apiRequest) {
        LOGGER.info("修改友链-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        String data = apiRequest.getData();
        if(data == null || StringUtils.isBlank(data)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            //friendLinkService.update(data);
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("修改友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取所有友链
     */
    @PostMapping("/fetchAll")
    public ApiResponse<String> allArticle(ApiRequest<String> apiRequest) {
        LOGGER.info("获取所有友链-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        String data = apiRequest.getData();
        if(data == null || StringUtils.isBlank(data)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            //boolean signUpFlag = friendLinkService.allArticle(data);
            boolean signUpFlag = true;
            LOGGER.info("获取所有友链-Controller层出参-signUpFlag={}", signUpFlag);

            if(signUpFlag) {
                return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
            } else {
                return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("获取所有友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }
}
