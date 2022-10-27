package com.hackyle.blog.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.consumer.entity.ArticleAuthorEntity;
import com.hackyle.blog.consumer.po.ArticleAuthorPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleAuthorMapper extends BaseMapper<ArticleAuthorEntity> {

    List<ArticleAuthorPo> selectByArticleIds(@Param("articleIds")List<Long> articleIds);
}




