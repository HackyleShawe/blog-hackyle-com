package com.hackyle.blog.business.mapper;

import com.hackyle.blog.business.entity.FeedbackMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedbackMessageMapper extends BaseMapper<FeedbackMessageEntity> {
    int logicDelByIds(@Param("idList") List<Long> idList);
}




