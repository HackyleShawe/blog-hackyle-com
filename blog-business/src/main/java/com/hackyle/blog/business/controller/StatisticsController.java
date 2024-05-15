package com.hackyle.blog.business.controller;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.service.StatisticsService;
import com.hackyle.blog.business.vo.StatisticsCountNumberVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 统计数量
     */
    @GetMapping("/countNumber")
    public ApiResponse<StatisticsCountNumberVo> countNumber() {
        try {
            StatisticsCountNumberVo numberVo= statisticsService.countNumber();
            LOGGER.info("统计数量-numberVo={}", JSON.toJSONString(numberVo));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), numberVo);
        } catch (Exception e) {
            LOGGER.error("统计数量-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

}
