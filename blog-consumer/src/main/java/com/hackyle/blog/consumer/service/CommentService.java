package com.hackyle.blog.consumer.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.CommentAddDto;
import com.hackyle.blog.consumer.entity.CommentEntity;
import com.hackyle.blog.consumer.vo.CommentVo;

import java.util.List;

public interface CommentService extends IService<CommentEntity> {
    ApiResponse<String> add(CommentAddDto addDto);

    List<CommentVo> fetchListByHierarchy(long targetId);
}
