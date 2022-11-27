package com.hackyle.blog.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.consumer.entity.FeedbackMessageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedbackMessageMapper extends BaseMapper<FeedbackMessageEntity> {
}
