package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ConfigurationAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.ConfigurationEntity;
import com.hackyle.blog.business.qo.ConfigurationQo;
import com.hackyle.blog.business.vo.ConfigurationVo;

public interface ConfigurationService extends IService<ConfigurationEntity> {
    ApiResponse<String> add(ConfigurationAddDto AddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String> update(ConfigurationAddDto UpdateDto);

    ConfigurationVo fetch(String id);

    PageResponseDto<ConfigurationVo> fetchList(PageRequestDto<ConfigurationQo> pageRequestDto);

}
