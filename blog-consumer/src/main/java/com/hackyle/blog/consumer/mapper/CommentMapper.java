package com.hackyle.blog.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.consumer.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
    List<CommentEntity> selectByParentIds(@Param("parentIdList") List<Long> parentIdList);

}




