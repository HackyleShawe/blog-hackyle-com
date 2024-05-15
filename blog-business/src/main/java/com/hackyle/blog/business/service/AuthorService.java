package com.hackyle.blog.business.service;

import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.AuthorAddDto;
import com.hackyle.blog.business.vo.AuthorVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

import java.util.List;

public interface AuthorService {
    ApiResponse<String> add(AuthorAddDto AddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String> update(AuthorAddDto UpdateDto);

    PageResponseDto<AuthorVo> fetchList(PageRequestDto<String> pageRequestDto);

    AuthorVo fetch(String id);

    List<AuthorVo> fetchAll();
}
