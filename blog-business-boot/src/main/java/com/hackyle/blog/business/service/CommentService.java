package com.hackyle.blog.business.service;

import com.hackyle.blog.business.dto.CommentAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.qo.CommentQo;
import com.hackyle.blog.business.vo.CommentVo;

import java.util.List;

public interface CommentService {
    void add(CommentAddDto addDto);

    void del(String ids);

    void delByTargetIds(List<Long> targetIdList);

    void update(CommentAddDto updateDto);

    PageResponseDto<CommentVo> fetchListByHierarchy(PageRequestDto<CommentQo> pageRequestDto);

    PageResponseDto<CommentVo> fetchList(PageRequestDto<CommentQo> pageRequestDto);
}
