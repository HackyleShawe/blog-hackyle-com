package com.hackyle.blog.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.consumer.entity.ArticleCategoryEntity;
import com.hackyle.blog.consumer.po.ArticleCategoryPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategoryEntity> {

    List<ArticleCategoryPo> selectByArticleIds(@Param("articleIds") List<Long> articleIds);
}




