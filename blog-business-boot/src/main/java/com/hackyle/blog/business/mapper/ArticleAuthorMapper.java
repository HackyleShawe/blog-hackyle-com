package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.ArticleAuthorEntity;
import com.hackyle.blog.business.po.ArticleAuthorPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleAuthorMapper extends BaseMapper<ArticleAuthorEntity> {

    int batchInsert(@Param("articleId") long articleId, @Param("authorIds") List<Long> authorIds);

    int batchDelByArticleIds(@Param("articleIds") List<Long> articleIds);

    int batchDelByAuthorIds(@Param("authorIds") List<Long> authorIds);

    List<ArticleAuthorPo> selectByArticleIds(@Param("articleIds")List<Long> articleIds);
}




