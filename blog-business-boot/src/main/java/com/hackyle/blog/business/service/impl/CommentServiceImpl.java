package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CommentAddDto;
import com.hackyle.blog.business.dto.OrderItemDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.CommentEntity;
import com.hackyle.blog.business.mapper.CommentMapper;
import com.hackyle.blog.business.qo.CommentQo;
import com.hackyle.blog.business.service.CommentService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity>
        implements CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;


    @Override
    public ApiResponse<String> add(CommentAddDto addDto) {
        CommentEntity commentEntity = BeanCopyUtils.copy(addDto, CommentEntity.class);

        commentEntity.setId(IDUtils.timestampID());
        int insert = commentMapper.insert(commentEntity);

        if(insert == 1) {
            throw new RuntimeException("新增评论失败");
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(Long.parseLong(idStr));
        }

        int updated = commentMapper.logicDeleteByIds(idList);
        if(updated < 1) {
            throw new RuntimeException("删除评论失败");
        }

        //不应该删除其子评论。因为在撤销删除时，子评论就全没了
        //commentMapper.logicDeleteByParentIds(idList);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ApiResponse<String> delReal(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> commentIds = new ArrayList<>();
        for (String idStr : idSplit) {
            commentIds.add(Long.parseLong(idStr));
        }

        this.removeByIds(commentIds);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ApiResponse<String> update(CommentAddDto updateDto) {
        CommentEntity commentEntity = BeanCopyUtils.copy(updateDto, CommentEntity.class);

        UpdateWrapper<CommentEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(CommentEntity::getId, updateDto.getId());
        int update = commentMapper.update(commentEntity, updateWrapper);
        if(update != 1) {
            throw new RuntimeException("评论更新失败");
        }

        //released有值，说明在修改评论的状态
        //if(null != updateDto.getReleased()) {
        //    //检查有没有子评论，子评论也要跟着修改
        //    CommentEntity updateEntity = new CommentEntity();
        //    updateEntity.setReleased(updateDto.getReleased());
        //
        //    UpdateWrapper<CommentEntity> upWrapper = new UpdateWrapper<>();
        //    upWrapper.lambda().eq(CommentEntity::getParentId, updateDto.getId());
        //    commentMapper.update(updateEntity, upWrapper);
        //}


        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public PageResponseDto<CommentVo> fetchListByHierarchy(PageRequestDto<CommentQo> pageRequestDto) {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CommentEntity::getParentId, -1L); //很重要，根据根父评论来分页
        //查询评论的层级，必须时未删除、已发布的
        queryWrapper.lambda().eq(CommentEntity::getDeleted, 0);
        queryWrapper.lambda().eq(CommentEntity::getReleased, 1);

        //根据更新时间逆序
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setColumn("update_time");
        orderItemDto.setAsc(false);
        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(orderItemDto);
        pageRequestDto.setOrders(orders);

        //分页查询父评论
        Page<CommentEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, CommentEntity.class);
        Page<CommentEntity> resultPage = commentMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("分页获取层级评论查询数据库-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
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

        ////Map：父评论ID，父评论对应的子评论
        //Map<Long, List<CommentEntity>> commentMap = commentRecords.stream().collect(Collectors.groupingBy(CommentEntity::getParentId, Collectors.toList()));
        ////按照父评论的更新时间进行逆序的排序
        //List<CommentEntity> parentComments = commentMap.get(-1L).stream().sorted(Comparator.comparing(CommentEntity::getUpdateTime).reversed()).collect(Collectors.toList());
        //
        //List<CommentVo> resultComments = new ArrayList<>(parentComments.size());
        //for (CommentEntity parentComment : parentComments) {
        //    CommentVo commentVo = BeanCopyUtils.copy(parentComment, CommentVo.class);
        //
        //    List<CommentEntity> subComments = commentMap.get(parentComment.getId());
        //    if(subComments != null && subComments.size() > 0) {
        //        List<CommentVo> replyList = BeanCopyUtils.copyList(subComments, CommentVo.class);
        //        commentVo.setReplyList(replyList);
        //    }
        //
        //    resultComments.add(commentVo);
        //}
        //
        //return new PageResponseDto<>(resultComments, resultPage.getTotal());
    }

    @Override
    public PageResponseDto<CommentVo> fetchList(PageRequestDto<CommentQo> pageRequestDto) {
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply(" 1 = 1 ");

        //组装查询条件
        CommentQo commentQo = pageRequestDto.getCondition();
        if(commentQo != null) {
            Boolean deleted = commentQo.getDeleted() != null && commentQo.getDeleted();
            queryWrapper.lambda().eq(CommentEntity::getDeleted, deleted);

            if(StringUtils.isNotBlank(commentQo.getName())) {
                queryWrapper.lambda().like(CommentEntity::getName, commentQo.getName());
            }
            if(StringUtils.isNotBlank(commentQo.getEmail())) {
                queryWrapper.lambda().like(CommentEntity::getEmail, commentQo.getEmail());
            }
            if(StringUtils.isNotBlank(commentQo.getContent())) {
                queryWrapper.lambda().like(CommentEntity::getContent, commentQo.getContent());
            }
            if(null != commentQo.getTargetId() && commentQo.getTargetId() > 0L) {
                queryWrapper.lambda().eq(CommentEntity::getTargetId, commentQo.getTargetId());
            }
            if(null != commentQo.getParentId()) {
                //userWrapper.lambda.and(tmp ->tmp.eq(“pwd”, pwd).or().eq(“phone”, phone));
                queryWrapper.lambda().and(
                        ele -> ele.eq(CommentEntity::getParentId, commentQo.getParentId())
                        .or()
                        .eq(CommentEntity::getId, commentQo.getParentId())
                );
            }
            if(null != commentQo.getReleased()) {
                queryWrapper.lambda().eq(CommentEntity::getReleased, commentQo.getReleased());
            }
        }

        //根据更新时间逆序
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setColumn("update_time");
        orderItemDto.setAsc(false);
        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(orderItemDto);
        pageRequestDto.setOrders(orders);

        Page<CommentEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, CommentEntity.class);
        Page<CommentEntity> resultPage = commentMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("分页获取评论查询数据库-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        return PaginationUtils.IPage2PageResponse(resultPage, CommentVo.class);
    }

}
