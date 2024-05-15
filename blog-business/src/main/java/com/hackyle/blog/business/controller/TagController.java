package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiRequest;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.TagAddDto;
import com.hackyle.blog.business.qo.TagQo;
import com.hackyle.blog.business.service.TagService;
import com.hackyle.blog.business.vo.TagVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;

    /**
     * 新增文章标签
     */
    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ApiRequest<TagAddDto> apiRequest) {
        TagAddDto tagAddDto = apiRequest.getData();
        LOGGER.info("新增文章标签-Controller层入参-tagAddDto={}", JSON.toJSONString(tagAddDto));

        if(tagAddDto == null || StringUtils.isBlank(tagAddDto.getName()) || StringUtils.isBlank(tagAddDto.getCode())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return tagService.add(tagAddDto);
        } catch (Exception e) {
            LOGGER.error("新增文章标签-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除文章标签
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids")String ids) {
        LOGGER.info("删除文章标签-Controller层入参-ids={}", ids);

        if(ids == null || StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return tagService.del(ids);
        } catch (Exception e) {
            LOGGER.error("删除文章标签-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改文章标签
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<TagAddDto> apiRequest) {
        TagAddDto addDto = apiRequest.getData();
        LOGGER.info("修改文章标签-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null || null == addDto.getId()) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return tagService.update(addDto);
        } catch (Exception e) {
            LOGGER.error("修改文章标签-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    /**
     * 获取文章标签详情
     */
    @GetMapping("/fetch")
    public ApiResponse<TagVo> fetch(@RequestParam("id") String id) {
        LOGGER.info("获取文章标签详情-Controller层入参-id={}", id);

        if(id == null || StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            TagVo tagVo = tagService.fetch(id);
            LOGGER.info("获取文章标签详情-Controller层出参-articleTagVo={}", JSON.toJSONString(tagVo));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), tagVo);
        } catch (Exception e) {
            LOGGER.error("获取文章标签详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 根据查询条件获取文章标签
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<TagVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<TagQo>> apiRequest) {
        LOGGER.info("根据查询条件获取文章标签-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        PageRequestDto<TagQo> pageRequestDto = apiRequest.getData();
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
            PageResponseDto<TagVo> pageResponseDto = tagService.fetchList(pageRequestDto);
            LOGGER.info("根据查询条件获取文章标签-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("根据查询条件获取文章标签-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取所有文章标签
     */
    @GetMapping("/fetchAll")
    public ApiResponse<List<TagVo>> fetchAll() {
        try {
            List<TagVo> tagVoList = tagService.fetchAll();
            LOGGER.info("获取所有文章标签-Controller层出参-tagVoList={}", JSON.toJSONString(tagVoList));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), tagVoList);
        } catch (Exception e) {
            LOGGER.error("获取所有文章标签-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }
}
