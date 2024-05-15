package com.hackyle.blog.business.service;

import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FriendLinkAddDto;
import com.hackyle.blog.business.qo.FriendLinkQo;
import com.hackyle.blog.business.vo.FriendLinkVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;

public interface FriendLinkService {
    ApiResponse<String> add(FriendLinkAddDto friendLinkAddDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String> update(FriendLinkAddDto friendLinkAddDto);

    FriendLinkVo fetch(String id);

    PageResponseDto<FriendLinkVo> fetchList(PageRequestDto<FriendLinkQo> pageRequestDto);
}
