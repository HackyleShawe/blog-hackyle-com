package com.hackyle.blog.business.service;

import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CategoryAddDto;
import com.hackyle.blog.business.qo.CategoryQo;
import com.hackyle.blog.business.vo.CategoryVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

import java.util.List;

public interface CategoryService {
    ApiResponse<String> add(CategoryAddDto categoryAddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String>  update(CategoryAddDto categoryAddDto);

    CategoryVo fetch(String id);

    PageResponseDto<CategoryVo> fetchList(PageRequestDto<CategoryQo> pageRequestDto);

    List<CategoryVo> fetchAll();
}