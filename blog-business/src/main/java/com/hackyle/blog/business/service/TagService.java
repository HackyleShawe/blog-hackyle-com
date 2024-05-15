package com.hackyle.blog.business.service;


import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.TagAddDto;
import com.hackyle.blog.business.qo.TagQo;
import com.hackyle.blog.business.vo.TagVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

import java.util.List;

public interface TagService {

    ApiResponse<String> add(TagAddDto tagAddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String>  update(TagAddDto addDto);

    TagVo fetch(String id);

    PageResponseDto<TagVo> fetchList(PageRequestDto<TagQo> pageRequestDto);

    List<TagVo> fetchAll();
}