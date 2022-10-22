package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.ArticleCategoryEntity;
import com.hackyle.blog.business.po.ArticleAuthorPo;
import com.hackyle.blog.business.po.ArticleCategoryPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategoryEntity> {

    int batchInsert(@Param("articleId") long articleId, @Param("categoryIdArr") String[] categoryIdArr);

    int batchDelByArticleIds(@Param("articleIds") List<Long> articleIds);

    int batchDelByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    List<ArticleCategoryPo> selectByArticleIds(@Param("idList") List<Long> idList);
}




