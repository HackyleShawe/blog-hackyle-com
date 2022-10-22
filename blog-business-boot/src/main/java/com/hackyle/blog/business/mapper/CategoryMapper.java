package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {
    int logicDeleteByIds(@Param("idList") List<Long> idList);
}




