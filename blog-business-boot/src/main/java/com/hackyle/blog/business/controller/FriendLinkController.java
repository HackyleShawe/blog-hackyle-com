package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FriendLinkAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.FriendLinkQo;
import com.hackyle.blog.business.service.FriendLinkService;
import com.hackyle.blog.business.vo.FriendLinkVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<String> add(@RequestBody ApiRequest<FriendLinkAddDto> apiRequest) {
        FriendLinkAddDto friendLinkAddDto = apiRequest.getData();
        LOGGER.info("新增友链-Controller层入参-friendLinkAddDto={}", JSON.toJSONString(friendLinkAddDto));

        if(friendLinkAddDto == null || StringUtils.isBlank(friendLinkAddDto.getLinkUrl())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return friendLinkService.add(friendLinkAddDto);
        } catch (Exception e) {
            LOGGER.error("新增友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除友链
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids")String ids) {
        LOGGER.info("删除友链-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return friendLinkService.del(ids);
        } catch (Exception e) {
            LOGGER.error("删除友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改友链
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<FriendLinkAddDto> apiRequest) {
        FriendLinkAddDto updateDto = apiRequest.getData();
        LOGGER.info("修改友链-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));

        if(updateDto == null || updateDto.getId() == null) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return friendLinkService.update(updateDto);
            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("修改友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取友链详情
     */
    @GetMapping("/fetch")
    public ApiResponse<FriendLinkVo> fetch(@RequestParam("id")String id) {
        LOGGER.info("获取友链详情-controller层入参-id={}", id);
        if(StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            FriendLinkVo friendLinkVo = friendLinkService.fetch(id);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), friendLinkVo);
        } catch (Exception e) {
            LOGGER.error("获取友链详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 分页获取所有友链
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<FriendLinkVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<FriendLinkQo>> apiRequest) {
        PageRequestDto<FriendLinkQo> pageRequestDto = apiRequest.getData();
        LOGGER.info("分页获取所有友链-Controller层入参-pageRequestDto={}", JSON.toJSONString(pageRequestDto));

        if(pageRequestDto == null) {
            //return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
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
            PageResponseDto<FriendLinkVo> pageResponseDto = friendLinkService.fetchList(pageRequestDto);
            LOGGER.info("获取所有友链-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("获取所有友链-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
