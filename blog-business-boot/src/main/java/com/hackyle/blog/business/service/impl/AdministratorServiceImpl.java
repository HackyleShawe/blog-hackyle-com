package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.common.pojo.JwtPayload;
import com.hackyle.blog.business.util.HashUtils;
import com.hackyle.blog.business.util.JwtUtils;
import com.hackyle.blog.business.vo.AdministratorVo;
import com.hackyle.blog.business.dto.AdminSignInDto;
import com.hackyle.blog.business.dto.AdminSignUpDto;
import com.hackyle.blog.business.entity.AdministratorEntity;
import com.hackyle.blog.business.mapper.AdministratorMapper;
import com.hackyle.blog.business.service.AdministratorService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorServiceImpl implements AdministratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministratorServiceImpl.class);

    @Autowired
    private AdministratorMapper administratorMapper;

    @Override
    public ApiResponse<String> singUp(AdminSignUpDto adminSignUpDto) {
        AdministratorEntity administratorEntity = BeanCopyUtils.copy(adminSignUpDto, AdministratorEntity.class);

        //查询是否有相同用户名的用户
        QueryWrapper<AdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AdministratorEntity::getUsername, adminSignUpDto.getUsername());
        Integer count = administratorMapper.selectCount(queryWrapper);
        if(count != 0) {
            return ApiResponse.error(ResponseEnum.SIGN_UP_FAIL.getCode(), ResponseEnum.SIGN_UP_FAIL.getMessage());
        }

        administratorEntity.setId(IDUtils.timestampID());
        administratorEntity.setPassword(HashUtils.sha256(adminSignUpDto.getPassword()));
        int insert = administratorMapper.insert(administratorEntity);

        if(insert != 1) {
            return ApiResponse.error(ResponseEnum.SIGN_UP_FAIL.getCode(), ResponseEnum.SIGN_UP_FAIL.getMessage());
        } else {
            return ApiResponse.success(ResponseEnum.SIGN_UP_OK.getCode(), ResponseEnum.SIGN_UP_OK.getMessage());
        }
    }

    @Override
    public AdministratorVo signIn(AdminSignInDto adminSignInDto) {
        QueryWrapper<AdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", adminSignInDto.getUsername())
                .eq("password", HashUtils.sha256(adminSignInDto.getPassword()));
        AdministratorEntity administratorEntity = administratorMapper.selectOne(queryWrapper);

        LOGGER.info("管理员登录查库-入参adminSignInDto={}，出参administratorEntity={}", JSON.toJSONString(adminSignInDto), JSON.toJSONString(administratorEntity));
        AdministratorVo administratorVo = BeanCopyUtils.copy(administratorEntity, AdministratorVo.class);

        //签发Token
        JwtPayload jwtPayload = new JwtPayload(String.valueOf(administratorVo.getId()), administratorVo.getUsername(), administratorVo.getUsername());
        String token = JwtUtils.createJWT(jwtPayload);
        administratorVo.setToken(token);

        return administratorVo;
    }

    @Override
    public AdministratorVo info(String token) {
        if(!JwtUtils.validateJWT(token)) {
            return null;
        }

        JwtPayload jwtPayload = JwtUtils.parseJWT(token);

        QueryWrapper<AdministratorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", Long.parseLong(jwtPayload.getId()))
            .eq("username", jwtPayload.getIssuer());
        AdministratorEntity administratorEntity = administratorMapper.selectOne(queryWrapper);

        return BeanCopyUtils.copy(administratorEntity, AdministratorVo.class);
    }

}
