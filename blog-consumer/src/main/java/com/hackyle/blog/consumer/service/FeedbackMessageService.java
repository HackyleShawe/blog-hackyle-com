package com.hackyle.blog.consumer.service;

import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.FeedbackMessageAddDto;

public interface FeedbackMessageService {
    ApiResponse<String> add(FeedbackMessageAddDto addDto);

}
