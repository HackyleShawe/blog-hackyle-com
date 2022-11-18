package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {

    int realDelByIds(@Param("idList") List<Long> idList);
    int realDelByParentIds(@Param("parentIdList")List<Long> parentIdList);
    int realDelByTargetIds(@Param("targetIdList")List<Long> targetIdList);

    int logicDeleteByIds(@Param("idList") List<Long> idList);
    int logicDeleteByParentIds(@Param("parentIdList")List<Long> parentIdList);
    int logicDeleteByTargetIds(@Param("targetIdList")List<Long> targetIdList);

    List<CommentEntity> selectByParentIds(@Param("parentIdList") List<Long> parentIdList);
}




