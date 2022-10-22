package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.vo.AdministratorVo;
import com.hackyle.blog.business.dto.AdminSignInDto;
import com.hackyle.blog.business.dto.AdminSignUpDto;
import com.hackyle.blog.business.service.AdministratorService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员
 */
@RestController
@RequestMapping("/admin")
public class AdministratorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministratorController.class);

    @Autowired
    private AdministratorService administratorService;


    /**
     * 管理员注册
     */
    @PostMapping(path = {"/sign-up", "/register"})
    public ApiResponse<String> signUp(@RequestBody ApiRequest<AdminSignUpDto> apiRequest) {
        LOGGER.info("注册-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        AdminSignUpDto adminSignUpDto = apiRequest.getData();
        if(adminSignUpDto == null || StringUtils.isBlank(adminSignUpDto.getUsername()) || StringUtils.isBlank(adminSignUpDto.getPassword())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return administratorService.singUp(adminSignUpDto);

        } catch (Exception e) {
            LOGGER.error("注册-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    /**
     * 管理员登录
     */
    @PostMapping(path = {"/sign-in", "/login"})
    public ApiResponse<AdministratorVo> signIn(@RequestBody AdminSignInDto adminSignInDto, HttpServletResponse response) {
        LOGGER.info("登录-Controller层入参-adminSignInDto={}", JSON.toJSONString(adminSignInDto));

        //AdminSignInDto adminSignInDto = apiRequest.getData();
        if(adminSignInDto == null || StringUtils.isBlank(adminSignInDto.getUsername()) || StringUtils.isBlank(adminSignInDto.getPassword())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            AdministratorVo administratorVo = administratorService.signIn(adminSignInDto);
            LOGGER.info("登录-Controller层出参-administratorDto={}", JSON.toJSONString(administratorVo));

            if(administratorVo != null) { //登录成功
                response.setHeader("Authorization", administratorVo.getToken());
                return ApiResponse.success(ResponseEnum.SIGN_IN_OK.getCode(), ResponseEnum.SIGN_IN_OK.getMessage(), administratorVo);
            } else {
                return ApiResponse.error(ResponseEnum.SIGN_IN_FAIL.getCode(), ResponseEnum.SIGN_IN_FAIL.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error("登录-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取登录者的信息
     */
    @GetMapping("/info")
    public ApiResponse<AdministratorVo> adminInfo(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getParameter("token");
        LOGGER.info("获取登录者的信息-Controller层出参-token={}", JSON.toJSONString(token));

        if(StringUtils.isBlank(token)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            AdministratorVo administratorVo = administratorService.info(token);

            LOGGER.info("获取登录者的信息-Controller层出参-administratorDto={}", JSON.toJSONString(administratorVo));

            if(administratorVo != null) {
                response.setHeader("Authorization", administratorVo.getToken());
                return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), administratorVo);
            } else {
                return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage());
            }

        } catch (Exception e) {
            LOGGER.error("注销失败：", e);
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage());
        }
    }

    /**
     * 管理员注销
     */
    @GetMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        //String username = request.getParameter("username");
        //if(username == null || "".equals(username.trim())) {
        //    return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        //}

        try {
            //清除签发的Token
            response.setHeader("Authorization", "");
            response.setHeader("X-Token", "");

            return ApiResponse.success(ResponseEnum.LOG_OUT_OK.getCode(), ResponseEnum.LOG_OUT_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("注销失败：", e);
            return ApiResponse.error(ResponseEnum.LOG_OUT_FAIL.getCode(), ResponseEnum.LOG_OUT_FAIL.getMessage());
        }
    }
}
