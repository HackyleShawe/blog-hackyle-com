package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.TagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<TagEntity> {
    int logicDeleteByIds(@Param("idList") List<Long> idList);

}




