package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<ArticleEntity> {
    int logicDeleteByIds(@Param("idList") List<Long> idList);

}




