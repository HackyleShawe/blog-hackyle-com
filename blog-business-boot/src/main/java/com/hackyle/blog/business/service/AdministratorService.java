package com.hackyle.blog.business.service;

import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.vo.AdministratorVo;
import com.hackyle.blog.business.dto.AdminSignInDto;
import com.hackyle.blog.business.dto.AdminSignUpDto;

public interface AdministratorService {
    ApiResponse<String> singUp(AdminSignUpDto adminSignUpDto);

    AdministratorVo signIn(AdminSignInDto adminSignInDto);

    AdministratorVo info(String token);
}
