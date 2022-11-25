package com.hackyle.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FeedbackMessageAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.FeedbackMessageEntity;
import com.hackyle.blog.business.qo.FeedbackMessageQo;
import com.hackyle.blog.business.vo.FeedbackMessageVo;

public interface FeedbackMessageService extends IService<FeedbackMessageEntity> {
    ApiResponse<String> add(FeedbackMessageAddDto addDto);

    ApiResponse<String> del(String ids);

    ApiResponse<String> update(FeedbackMessageAddDto updateDto);

    FeedbackMessageVo fetch(String id);

    PageResponseDto<FeedbackMessageVo> fetchList(PageRequestDto<FeedbackMessageQo> pageRequestDto);
}
