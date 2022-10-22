package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.business.dto.CategoryCountDto;
import com.hackyle.blog.business.dto.TagCountDto;
import com.hackyle.blog.business.service.StatisticsService;
import com.hackyle.blog.business.vo.CategoryCountVo;
import com.hackyle.blog.business.vo.TagCountVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsServiceImpl.class);
}
