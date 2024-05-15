package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.ArticleTagEntity;
import com.hackyle.blog.business.po.ArticleTagPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTagEntity> {

    int batchInsert(@Param("articleId") long articleId, @Param("tagIds") List<Long> tagIds);

    int batchDelByArticleIds(@Param("articleIds") List<Long> articleIds);

    int batchDelByTagIds(@Param("tagIds") List<Long> tagIds);

    List<ArticleTagPo> selectByArticleIds(@Param("articleIds") List<Long> articleIds);
}




