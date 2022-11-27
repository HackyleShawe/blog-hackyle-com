package com.hackyle.blog.consumer.service.impl;

import com.hackyle.blog.consumer.common.constant.ResponseEnum;
import com.hackyle.blog.consumer.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.FeedbackMessageAddDto;
import com.hackyle.blog.consumer.entity.FeedbackMessageEntity;
import com.hackyle.blog.consumer.mapper.FeedbackMessageMapper;
import com.hackyle.blog.consumer.service.FeedbackMessageService;
import com.hackyle.blog.consumer.util.BeanCopyUtils;
import com.hackyle.blog.consumer.util.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMessageServiceImpl implements FeedbackMessageService {
    @Autowired
    private FeedbackMessageMapper feedbackMessageMapper;

    @Override
    public ApiResponse<String> add(FeedbackMessageAddDto addDto) {
        FeedbackMessageEntity entity = BeanCopyUtils.copy(addDto, FeedbackMessageEntity.class);
        entity.setId(IDUtils.timestampID());

        int insert = feedbackMessageMapper.insert(entity);
        if(insert != 1) {
            throw new RuntimeException("新增留言反馈失败");
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }
}
