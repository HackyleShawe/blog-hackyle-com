package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FeedbackMessageAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.FeedbackMessageEntity;
import com.hackyle.blog.business.mapper.FeedbackMessageMapper;
import com.hackyle.blog.business.qo.FeedbackMessageQo;
import com.hackyle.blog.business.service.FeedbackMessageService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.FeedbackMessageVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackMessageServiceImpl extends ServiceImpl<FeedbackMessageMapper, FeedbackMessageEntity>
    implements FeedbackMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackMessageServiceImpl.class);

    @Autowired
    private FeedbackMessageMapper feedbackMessageMapper;

    @Override
    public ApiResponse<String> add(FeedbackMessageAddDto addDto) {
        FeedbackMessageEntity entity = BeanCopyUtils.copy(addDto, FeedbackMessageEntity.class);
        entity.setId(IDUtils.timestampID());

        int insert = feedbackMessageMapper.insert(entity);
        if(insert != 1) {
            throw new RuntimeException("新增留言反馈失败");
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

        feedbackMessageMapper.logicDelByIds(idList);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ApiResponse<String> update(FeedbackMessageAddDto updateDto) {
        FeedbackMessageEntity entity = BeanCopyUtils.copy(updateDto, FeedbackMessageEntity.class);

        UpdateWrapper<FeedbackMessageEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(FeedbackMessageEntity::getId, updateDto.getId());
        int update = feedbackMessageMapper.update(entity, updateWrapper);
        if(update != 1) {
            throw new RuntimeException("留言反馈更新失败");
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public FeedbackMessageVo fetch(String id) {
        long idd = IDUtils.decryptByAES(id);

        FeedbackMessageEntity entity = feedbackMessageMapper.selectById(idd);
        LOGGER.info("获取留言反馈-入参-idd={}-数据库查询结果-article={}", idd, JSON.toJSONString(entity));

        FeedbackMessageVo feedbackMessageVo = BeanCopyUtils.copy(entity, FeedbackMessageVo.class);

        return feedbackMessageVo;
    }

    @Override
    public PageResponseDto<FeedbackMessageVo> fetchList(PageRequestDto<FeedbackMessageQo> pageRequestDto) {
        QueryWrapper<FeedbackMessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply(" 1 = 1 ");

        //组装查询条件
        FeedbackMessageQo feedbackMessageQo = pageRequestDto.getCondition();
        if(feedbackMessageQo != null) {
            if(feedbackMessageQo.getDeleted() != null) {
                queryWrapper.eq("is_deleted", feedbackMessageQo.getDeleted());
            }
            if(feedbackMessageQo.getReleased() != null) {
                queryWrapper.eq("is_released", feedbackMessageQo.getReleased());
            }
            if(StringUtils.isNotBlank(feedbackMessageQo.getName())) {
                queryWrapper.lambda().like(FeedbackMessageEntity::getName, feedbackMessageQo.getName());
            }
            if(StringUtils.isNotBlank(feedbackMessageQo.getEmail())) {
                queryWrapper.lambda().like(FeedbackMessageEntity::getEmail, feedbackMessageQo.getEmail());
            }
            if(StringUtils.isNotBlank(feedbackMessageQo.getPhone())) {
                queryWrapper.lambda().like(FeedbackMessageEntity::getPhone, feedbackMessageQo.getPhone());
            }
            if(StringUtils.isNotBlank(feedbackMessageQo.getLink())) {
                queryWrapper.lambda().like(FeedbackMessageEntity::getLink, feedbackMessageQo.getLink());
            }
            if(StringUtils.isNotBlank(feedbackMessageQo.getContent())) {
                queryWrapper.lambda().like(FeedbackMessageEntity::getContent, feedbackMessageQo.getContent());
            }
        }

        //分页查询操作
        Page<FeedbackMessageEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, FeedbackMessageEntity.class);
        Page<FeedbackMessageEntity> resultPage = feedbackMessageMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("分页获取所有留言反馈-条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        PageResponseDto<FeedbackMessageVo> pageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, FeedbackMessageVo.class);

        return pageResponseDto;
    }
}




