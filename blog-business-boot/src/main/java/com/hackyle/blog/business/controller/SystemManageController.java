package com.hackyle.blog.business.controller;

import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.service.SystemManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/system")
public class SystemManageController {
    @Autowired
    private SystemManageService systemManageService;

    @GetMapping("/systemStatus")
    public ApiResponse<Map<String, Object>> systemStatus() {
        Map<String, Object> systemStatusMap = systemManageService.systemStatus();

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), systemStatusMap);
    }
}
