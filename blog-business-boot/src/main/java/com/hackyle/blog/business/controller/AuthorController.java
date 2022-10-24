package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.AuthorAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.service.AuthorService;
import com.hackyle.blog.business.vo.AuthorVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private AuthorService authorService;

    /**
     * 新增作者
     */
    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ApiRequest<AuthorAddDto> apiRequest) {
        LOGGER.info("新增作者-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        AuthorAddDto addDto = apiRequest.getData();
        if(addDto == null || StringUtils.isBlank(addDto.getNickName())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return authorService.add(addDto);
        } catch (Exception e) {
            LOGGER.error("新增作者-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除作者
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids") String ids) {
        LOGGER.info("删除作者-Controller层入参-ids={}", ids);

        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return authorService.del(ids);
        } catch (Exception e) {
            LOGGER.error("删除作者-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改作者
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<AuthorAddDto> apiRequest) {
        AuthorAddDto addDto = apiRequest.getData();
        LOGGER.info("修改作者-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null || null == addDto.getId()) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return authorService.update(addDto);
        } catch (Exception e) {
            LOGGER.error("修改作者-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 作者详情
     */
    @GetMapping("/fetch")
    public ApiResponse<AuthorVo> fetch(@RequestParam("id") String id) {
        LOGGER.info("获取作者详情-controller层入参-id={}", id);

        if(StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            AuthorVo authorVo = authorService.fetch(id);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), authorVo);
        } catch (Exception e) {
            LOGGER.error("获取作者详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 分页获取所有作者
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<AuthorVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<String>> apiRequest) {
        LOGGER.info("获取所有作者-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        PageRequestDto<String> pageRequestDto = apiRequest.getData();
        if(pageRequestDto == null) {
            //return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
            pageRequestDto = new PageRequestDto<String>();
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
            PageResponseDto<AuthorVo> pageResponseDto = authorService.fetchList(pageRequestDto);
            LOGGER.info("获取所有作者-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("获取所有作者-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取所有作者
     */
    @GetMapping("/fetchAll")
    public ApiResponse<List<AuthorVo>> fetchAll() {
        try {
            List<AuthorVo> authorVoList = authorService.fetchAll();
            LOGGER.info("获取所有作者-Controller层出参-authorVoList={}", JSON.toJSONString(authorVoList));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), authorVoList);
        } catch (Exception e) {
            LOGGER.error("获取所有作者-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }
}
