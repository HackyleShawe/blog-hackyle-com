package com.hackyle.blog.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.consumer.entity.ArticleTagEntity;
import com.hackyle.blog.consumer.po.ArticleTagPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTagEntity> {

    List<ArticleTagPo> selectByArticleIds(@Param("articleIds") List<Long> articleIds);
}




