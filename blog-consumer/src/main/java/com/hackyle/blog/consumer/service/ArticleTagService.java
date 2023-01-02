package com.hackyle.blog.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.consumer.dto.PageRequestDto;
import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.entity.ArticleTagEntity;
import com.hackyle.blog.consumer.po.ArticleTagPo;
import com.hackyle.blog.consumer.qo.TagQo;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.consumer.vo.TagVo;

import java.util.List;
import java.util.Map;

public interface ArticleTagService extends IService<ArticleTagEntity> {
    Map<Long, List<ArticleTagPo>> selectByArticleIds(List<Long> articleIds);

    List<TagVo> selectTag();

    PageResponseDto<ArticleVo> selectArticleByTag(PageRequestDto<TagQo> pageRequestDto);

}
