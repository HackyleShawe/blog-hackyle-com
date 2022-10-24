package com.hackyle.blog.business.service;

import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CategoryAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.CategoryQo;
import com.hackyle.blog.business.vo.CategoryVo;

import java.util.List;


public interface CategoryService {
    ApiResponse<String> add(CategoryAddDto categoryAddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String>  update(CategoryAddDto categoryAddDto);

    CategoryVo fetch(String id);

    PageResponseDto<CategoryVo> fetchList(PageRequestDto<CategoryQo> pageRequestDto);

    List<CategoryVo> fetchAll();
}