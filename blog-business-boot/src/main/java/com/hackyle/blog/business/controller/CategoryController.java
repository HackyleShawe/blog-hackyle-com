package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiRequest;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CategoryAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.CategoryQo;
import com.hackyle.blog.business.service.CategoryService;
import com.hackyle.blog.business.vo.CategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增文章分类
     */
    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ApiRequest<CategoryAddDto> apiRequest) {
        LOGGER.info("新增文章分类-Controller层入参-apiRequest={}", JSON.toJSONString(apiRequest));

        CategoryAddDto categoryAddDto = apiRequest.getData();
        if(categoryAddDto == null || StringUtils.isBlank(categoryAddDto.getName()) || StringUtils.isBlank(categoryAddDto.getCode())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return categoryService.add(categoryAddDto);
        } catch (Exception e) {
            LOGGER.error("新增文章分类-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除文章分类
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids")String ids) {
        LOGGER.info("删除文章分类-Controller层入参-ids={}", ids);
        if(ids == null || StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return categoryService.del(ids);
        } catch (Exception e) {
            LOGGER.error("删除文章分类-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改文章分类
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<CategoryAddDto> apiRequest) {
        CategoryAddDto categoryAddDto = apiRequest.getData();
        LOGGER.info("修改文章分类-Controller层入参-categoryAddDto={}", JSON.toJSONString(categoryAddDto));

        if(categoryAddDto == null || categoryAddDto.getId() == null) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return categoryService.update(categoryAddDto);
        } catch (Exception e) {
            LOGGER.error("修改文章分类-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取文章分类详情
     */
    @DeleteMapping("/fetch")
    public ApiResponse<CategoryVo> fetch(@RequestParam("id") String id) {
        LOGGER.info("获取文章分类详情-Controller层入参-id={}", id);

        if(id == null || StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            CategoryVo categoryVo = categoryService.fetch(id);
            LOGGER.info("获取文章分类详情-Controller层出参-articleCategoryVo={}", JSON.toJSONString(categoryVo));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), categoryVo);
        } catch (Exception e) {
            LOGGER.error("获取文章分类详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 根据查询条件获取文章分类
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<CategoryVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<CategoryQo>> apiRequest) {
        PageRequestDto<CategoryQo> pageRequestDto = apiRequest.getData();
        LOGGER.info("根据查询条件获取文章分类-Controller层入参-pageRequestDto={}", JSON.toJSONString(pageRequestDto));

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
            PageResponseDto<CategoryVo> pageResponseDto = categoryService.fetchList(pageRequestDto);
            LOGGER.info("根据查询条件获取文章分类-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("根据查询条件获取文章分类-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取所有文章分类
     */
    @GetMapping("/fetchAll")
    public ApiResponse<List<CategoryVo>> fetchAll() {
        try {
            List<CategoryVo> categoryVoList = categoryService.fetchAll();
            LOGGER.info("获取所有文章分类-Controller层出参-articleCategoryVoList={}", JSON.toJSONString(categoryVoList));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), categoryVoList);
        } catch (Exception e) {
            LOGGER.error("获取所有文章分类-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
