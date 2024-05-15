package com.hackyle.blog.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hackyle.blog.business.entity.FileStorageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileStorageMapper extends BaseMapper<FileStorageEntity> {
    Integer batchInsertImg(@Param("imgList") List<String> imgList);

    Integer batchInsert(@Param("article_uri") String articleUri, @Param("imgList") List<String> imgList);

    Integer updateArticleUriById(@Param("article_uri") String articleUri, @Param("idList") List<Long> idList);

    List<FileStorageEntity> selectByArticleUri(@Param("article_uri") String articleUri);

    List<FileStorageEntity> selectByArticleUriIsNull();

}
