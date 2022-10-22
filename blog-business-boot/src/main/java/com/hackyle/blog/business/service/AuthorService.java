package com.hackyle.blog.business.service;

import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.AuthorAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.vo.AuthorVo;

import java.util.List;

public interface AuthorService {
    ApiResponse<String> add(AuthorAddDto AddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String> update(AuthorAddDto UpdateDto);

    PageResponseDto<AuthorVo> fetchList(PageRequestDto<String> pageRequestDto);

    AuthorVo fetch(long idd);

    List<AuthorVo> fetchAll();
}
