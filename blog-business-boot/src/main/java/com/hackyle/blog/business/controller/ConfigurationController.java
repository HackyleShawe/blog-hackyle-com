package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ConfigurationAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.ConfigurationQo;
import com.hackyle.blog.business.service.ConfigurationService;
import com.hackyle.blog.business.vo.ConfigurationVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigurationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    private ConfigurationService configurationService;

    /**
     * 新增配置
     */
    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ApiRequest<ConfigurationAddDto> apiRequest) {
        LOGGER.info("新增配置-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        ConfigurationAddDto addDto = apiRequest.getData();
        //if(addDto == null || StringUtils.isBlank(addDto.getNickName())) {
        //    return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        //}

        try {
            return configurationService.add(addDto);
        } catch (Exception e) {
            LOGGER.error("新增配置-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids") String ids) {
        LOGGER.info("删除配置-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return configurationService.del(ids);
        } catch (Exception e) {
            LOGGER.error("删除配置-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改配置
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<ConfigurationAddDto> apiRequest) {
        ConfigurationAddDto addDto = apiRequest.getData();
        LOGGER.info("修改配置-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null || null == addDto.getId()) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return configurationService.update(addDto);
        } catch (Exception e) {
            LOGGER.error("修改配置-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 配置详情
     */
    @GetMapping("/fetch")
    public ApiResponse<ConfigurationVo> fetch(@RequestParam("id") String id) {
        LOGGER.info("获取配置详情-controller层入参-id={}", id);

        if(StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            ConfigurationVo authorVo = configurationService.fetch(id);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), authorVo);
        } catch (Exception e) {
            LOGGER.error("获取配置详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 分页获取所有配置
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<ConfigurationVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<ConfigurationQo>> apiRequest) {
        LOGGER.info("获取所有配置-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        PageRequestDto<ConfigurationQo> pageRequestDto = apiRequest.getData();
        if(pageRequestDto == null) {
            pageRequestDto = new PageRequestDto<>();
            pageRequestDto.setCurrentPage(1);
            pageRequestDto.setPageSize(10);
        }

        //纠正不合法数据
        if(pageRequestDto.getPageSize() < 1) {
            pageRequestDto.setPageSize(10);
        }
        if(pageRequestDto.getCurrentPage() < 1) {
            pageRequestDto.setCurrentPage(1);
        }

        try {
            PageResponseDto<ConfigurationVo> pageResponseDto = configurationService.fetchList(pageRequestDto);
            LOGGER.info("获取所有配置-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("获取所有配置-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }
}
