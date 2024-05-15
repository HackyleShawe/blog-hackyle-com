package com.hackyle.blog.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.business.entity.ArticleAccessEntity;
import com.hackyle.blog.business.mapper.ArticleAccessMapper;
import com.hackyle.blog.business.qo.ArticleAccessLogQo;
import com.hackyle.blog.business.service.LoggerService;
import com.hackyle.blog.business.vo.ArticleAccessLogVo;
import com.hackyle.blog.common.util.PaginationUtils;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoggerServiceImpl implements LoggerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerServiceImpl.class);

    @Autowired
    private ArticleAccessMapper articleAccessMapper;

    @Override
    public PageResponseDto<ArticleAccessLogVo> articleAccess(PageRequestDto<ArticleAccessLogQo> pageRequestDto) {
        QueryWrapper<ArticleAccessEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        //组装查询条件
        ArticleAccessLogQo articleAccessLogQo = pageRequestDto.getCondition();
        if(articleAccessLogQo != null) {
            if(StringUtils.isNotBlank(articleAccessLogQo.getArticleUri())) {
                queryWrapper.lambda().like(ArticleAccessEntity::getArticleUri, articleAccessLogQo.getArticleUri());
            }
            if(StringUtils.isNotBlank(articleAccessLogQo.getIp())) {
                queryWrapper.lambda().like(ArticleAccessEntity::getIp, articleAccessLogQo.getIp());
            }
            if(StringUtils.isNotBlank(articleAccessLogQo.getAddress())) {
                queryWrapper.lambda().like(ArticleAccessEntity::getAddress, articleAccessLogQo.getAddress());
            }
            if(StringUtils.isNotBlank(articleAccessLogQo.getBrowser())) {
                queryWrapper.lambda().like(ArticleAccessEntity::getBrowser, articleAccessLogQo.getBrowser());
            }
            if(null != articleAccessLogQo.getTimeUse() && articleAccessLogQo.getTimeUse() > 0) {
                queryWrapper.lambda().le(ArticleAccessEntity::getTimeUse, articleAccessLogQo.getTimeUse());
            }
            if(null != articleAccessLogQo.getTimePicker() && articleAccessLogQo.getTimePicker().length > 1) {
                Date[] timePicker = articleAccessLogQo.getTimePicker();
                queryWrapper.lambda().between(ArticleAccessEntity::getCreateTime, timePicker[0], timePicker[1]);
                //queryWrapper.lambda().ge(ArticleAccessEntity::getCreateTime, timePicker[0]);
                //queryWrapper.lambda().le(ArticleAccessEntity::getCreateTime, timePicker[1]);
            }
        }
        queryWrapper.lambda().orderByDesc(ArticleAccessEntity::getCreateTime);

        //分页操作
        Page<ArticleAccessEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, ArticleAccessEntity.class);
        Page<ArticleAccessEntity> resultPage = articleAccessMapper.selectPage(paramPage, queryWrapper);
        //LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
        //        JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        return PaginationUtils.IPage2PageResponse(resultPage, ArticleAccessLogVo.class);
    }
}
