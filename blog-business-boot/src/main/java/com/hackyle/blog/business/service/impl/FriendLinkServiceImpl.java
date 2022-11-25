package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FriendLinkAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.FriendLinkEntity;
import com.hackyle.blog.business.mapper.FriendLinkMapper;
import com.hackyle.blog.business.qo.FriendLinkQo;
import com.hackyle.blog.business.service.FriendLinkService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.FriendLinkVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendLinkServiceImpl implements FriendLinkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendLinkServiceImpl.class);

    @Autowired
    private FriendLinkMapper friendLinkMapper;

    @Override
    public ApiResponse<String> add(FriendLinkAddDto friendLinkAddDto) {
        FriendLinkEntity friendLinkEntity = BeanCopyUtils.copy(friendLinkAddDto, FriendLinkEntity.class);

        //检查一下友链是否已经存在
        QueryWrapper<FriendLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FriendLinkEntity::getLinkUrl, friendLinkEntity.getLinkUrl());
        FriendLinkEntity checkFriendLinkEntity = friendLinkMapper.selectOne(queryWrapper);

        if(checkFriendLinkEntity != null) {
            //友链已存在，执行更新操作
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "友链已存在");

        } else {
            //友链不存在，执行新增操作
            friendLinkEntity.setId(IDUtils.timestampID());
            int inserted = friendLinkMapper.insert(friendLinkEntity);
            if(inserted != 1) {
                throw new RuntimeException("新增友链失败");
            }
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

        friendLinkMapper.logicDeleteByIds(idList);

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public ApiResponse<String> update(FriendLinkAddDto friendLinkAddDto) {
        FriendLinkEntity friendLinkEntity = BeanCopyUtils.copy(friendLinkAddDto, FriendLinkEntity.class);

        UpdateWrapper<FriendLinkEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(FriendLinkEntity::getId, friendLinkAddDto.getId());
        int update = friendLinkMapper.update(friendLinkEntity, updateWrapper);
        if(update != 1) {
            throw new RuntimeException("友链更新失败");
        }

        return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
    }

    @Override
    public FriendLinkVo fetch(String id) {
        long idd = IDUtils.decryptByAES(id);

        FriendLinkEntity friendLinkEntity = friendLinkMapper.selectById(idd);
        LOGGER.info("获取友链-入参-idd={}-数据库查询结果-article={}", idd, JSON.toJSONString(friendLinkEntity));

        FriendLinkVo friendLinkVo = BeanCopyUtils.copy(friendLinkEntity, FriendLinkVo.class);

        return friendLinkVo;
    }

    @Override
    public PageResponseDto<FriendLinkVo> fetchList(PageRequestDto<FriendLinkQo> pageRequestDto) {
        QueryWrapper<FriendLinkEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply(" 1 = 1 ");

        //组装查询条件
        FriendLinkQo friendLinkQo = pageRequestDto.getCondition();
        if(friendLinkQo != null) {
            if(friendLinkQo.getDeleted() != null) {
                queryWrapper.eq("is_deleted", friendLinkQo.getDeleted());
            }
            if(friendLinkQo.getReleased() != null) {
                queryWrapper.eq("is_released", friendLinkQo.getReleased());
            }
            if(StringUtils.isNotBlank(friendLinkQo.getLinkUrl())) {
                queryWrapper.lambda().like(FriendLinkEntity::getLinkUrl, friendLinkQo.getLinkUrl());
            }
            if(StringUtils.isNotBlank(friendLinkQo.getName())) {
                queryWrapper.lambda().like(FriendLinkEntity::getName, friendLinkQo.getName());
            }
            if(StringUtils.isNotBlank(friendLinkQo.getDescription())) {
                queryWrapper.lambda().like(FriendLinkEntity::getDescription, friendLinkQo.getDescription());
            }
        }

        //分页查询操作
        Page<FriendLinkEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, FriendLinkEntity.class);
        Page<FriendLinkEntity> resultPage = friendLinkMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("分页获取所有友链-条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        PageResponseDto<FriendLinkVo> pageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, FriendLinkVo.class);

        return pageResponseDto;
    }
}
