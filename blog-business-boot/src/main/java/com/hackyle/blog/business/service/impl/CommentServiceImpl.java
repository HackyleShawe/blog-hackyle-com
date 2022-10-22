package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.business.dto.CommentAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.CommentEntity;
import com.hackyle.blog.business.mapper.CommentMapper;
import com.hackyle.blog.business.qo.CommentQo;
import com.hackyle.blog.business.service.CommentService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.CommentVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;
    
    
    @Override
    public void add(CommentAddDto addDto) {
        CommentEntity commentEntity = BeanCopyUtils.copy(addDto, CommentEntity.class);

        int insert = commentMapper.insert(commentEntity);

        if(insert == 1) {
            throw new RuntimeException("新增评论失败");
        }
    }

    @Override
    public void del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(Long.parseLong(idStr));
        }

        int updated = commentMapper.logicDeleteByIds(idList);
        if(updated < 1) {
            throw new RuntimeException("删除评论失败");
        }
        //更新子评论为删除状态
        commentMapper.logicDeleteByParentIds(idList);

    }

    @Override
    public void delByTargetIds(List<Long> targetIdList) {
        if(targetIdList.isEmpty()) {
            return;
        }

        commentMapper.logicDeleteByTargetIds(targetIdList);
    }

    @Override
    public void update(CommentAddDto updateDto) {
        CommentEntity commentEntity = BeanCopyUtils.copy(updateDto, CommentEntity.class);

        UpdateWrapper<CommentEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(CommentEntity::getId, updateDto.getId());
        int update = commentMapper.update(commentEntity, updateWrapper);
        if(update != 1) {
            throw new RuntimeException("评论更新失败");
        }

        //检查是否有子评论
    }

    @Override
    public PageResponseDto<CommentVo> fetchListByHierarchy(PageRequestDto<CommentQo> pageRequestDto) {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CommentEntity::getParentId, -1); //很重要，根据根父评论来分页

        //组装查询条件
        CommentQo commentQo = pageRequestDto.getCondition();
        if(StringUtils.isNotBlank(commentQo.getName())) {
            queryWrapper.lambda().like(CommentEntity::getName, commentQo.getName());
        }
        if(StringUtils.isNotBlank(commentQo.getEmail())) {
            queryWrapper.lambda().like(CommentEntity::getEmail, commentQo.getEmail());
        }
        if(StringUtils.isNotBlank(commentQo.getContent())) {
            queryWrapper.lambda().like(CommentEntity::getContent, commentQo.getContent());
        }
        if(commentQo.getTargetId() != null && commentQo.getTargetId() > 0L) {
            queryWrapper.lambda().eq(CommentEntity::getTargetId, commentQo.getTargetId());
        }
        if(null != commentQo.getReleased()) {
            queryWrapper.lambda().eq(CommentEntity::getReleased, commentQo.getReleased());
        }
        Boolean deleted = commentQo.getDeleted() != null && commentQo.getDeleted();
        queryWrapper.lambda().eq(CommentEntity::getDeleted, deleted);

        //分页查询操作
        Page<CommentEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, CommentEntity.class);
        Page<CommentEntity> resultPage = commentMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("分页获取层级评论-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        List<CommentEntity> parentCommentEntityList = resultPage.getRecords();
        PageResponseDto<CommentVo> commentVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, CommentVo.class);

        //没有数据，直接返回
        if(null == parentCommentEntityList || parentCommentEntityList.isEmpty()) {
            return commentVoPageResponseDto;
        }

        //收集根父评论的ID
        List<Long> parentIdList = parentCommentEntityList.stream()
                .map(CommentEntity::getId).collect(Collectors.toList());

        //收集父评论下的所有子评论
        List<CommentEntity> childCommentList = commentMapper.selectByParentIds(parentIdList);

        Map<Long, List<CommentEntity>> groupByParentIdMap = childCommentList.stream().collect(Collectors.groupingBy(CommentEntity::getParentId));

        List<CommentVo> resultList = new ArrayList<>();
        for (CommentEntity parentEntity : parentCommentEntityList) {
            CommentVo commentVo = BeanCopyUtils.copy(parentEntity, CommentVo.class);
            //收集子评论
            List<CommentEntity> tmpReplyList = groupByParentIdMap.get(parentEntity.getId());
            //List<CommentEntity> tmpReplyList = parentCommentEntityList.stream()
            //        .filter(ele -> Objects.equals(ele.getParentId(), parentEntity.getId())).collect(Collectors.toList());
            List<CommentVo> replyList = BeanCopyUtils.copyList(tmpReplyList, CommentVo.class);

            commentVo.setReplyList(replyList);

            resultList.add(commentVo);
        }

        commentVoPageResponseDto.setRows(resultList);
        return commentVoPageResponseDto;
    }

    @Override
    public PageResponseDto<CommentVo> fetchList(PageRequestDto<CommentQo> pageRequestDto) {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();

        //组装查询条件
        CommentQo commentQo = pageRequestDto.getCondition();
        if(StringUtils.isNotBlank(commentQo.getName())) {
            queryWrapper.lambda().like(CommentEntity::getName, commentQo.getName());
        }
        if(StringUtils.isNotBlank(commentQo.getEmail())) {
            queryWrapper.lambda().like(CommentEntity::getEmail, commentQo.getEmail());
        }
        if(StringUtils.isNotBlank(commentQo.getContent())) {
            queryWrapper.lambda().like(CommentEntity::getContent, commentQo.getContent());
        }
        if(commentQo.getTargetId() != null && commentQo.getTargetId() > 0L) {
            queryWrapper.lambda().eq(CommentEntity::getTargetId, commentQo.getTargetId());
        }
        if(null != commentQo.getReleased()) {
            queryWrapper.lambda().eq(CommentEntity::getReleased, commentQo.getReleased());
        }
        Boolean deleted = commentQo.getDeleted() != null && commentQo.getDeleted();
        queryWrapper.lambda().eq(CommentEntity::getDeleted, deleted);


        //分页获取未审查的评论
        Page<CommentEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, CommentEntity.class);
        Page<CommentEntity> resultPage = commentMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        return PaginationUtils.IPage2PageResponse(resultPage, CommentVo.class);
    }

}
