package com.hackyle.blog.business.service;

import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FriendLinkAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.FriendLinkQo;
import com.hackyle.blog.business.vo.FriendLinkVo;

public interface FriendLinkService {
    ApiResponse<String> add(FriendLinkAddDto friendLinkAddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String> update(FriendLinkAddDto friendLinkAddDto);

    FriendLinkVo fetch(String id);

    PageResponseDto<FriendLinkVo> fetchList(PageRequestDto<FriendLinkQo> pageRequestDto);
}
