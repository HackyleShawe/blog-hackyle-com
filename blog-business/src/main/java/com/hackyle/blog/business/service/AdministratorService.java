package com.hackyle.blog.business.service;

import com.hackyle.blog.business.vo.AdministratorVo;
import com.hackyle.blog.business.dto.AdminSignInDto;
import com.hackyle.blog.business.dto.AdminSignUpDto;
import com.hackyle.blog.business.vo.KaptchaVO;
import com.hackyle.blog.common.pojo.ApiResponse;

public interface AdministratorService {
    ApiResponse<String> singUp(AdminSignUpDto adminSignUpDto);

    ApiResponse<String> update(AdminSignUpDto adminSignUpDto);

    AdministratorVo signIn(AdminSignInDto adminSignInDto);

    AdministratorVo info(String token);

    KaptchaVO verificationCode();

}
