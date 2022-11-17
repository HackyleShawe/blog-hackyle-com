package com.hackyle.blog.business.controller;

import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.service.StatisticsService;
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
     * 统计文章数
     */
    @GetMapping("/countArticles")
    public ApiResponse<Integer> countArticles() {
        try {
            Integer count = statisticsService.countArticles();
            LOGGER.info("统计文章数-count={}", count);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), count);
        } catch (Exception e) {
            LOGGER.error("统计文章数-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 统计文章分类数
     */
    @GetMapping("/countCategories")
    public ApiResponse<Integer> countCategories() {
        try {
            Integer count = statisticsService.countCategories();
            LOGGER.info("统计文章分类数-count={}", count);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), count);
        } catch (Exception e) {
            LOGGER.error("统计文章分类数-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 统计文章标签数
     */
    @GetMapping("/countTags")
    public ApiResponse<Integer> countTags() {
        try {
            Integer count = statisticsService.countTags();
            LOGGER.info("统计文章标签数-count={}", count);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), count);
        } catch (Exception e) {
            LOGGER.error("统计文章标签数-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 统计文章评论数
     */
    @GetMapping("/countComments")
    public ApiResponse<Integer> countComments() {
        try {
            Integer count = statisticsService.countComments();
            LOGGER.info("统计文章评论数-count={}", count);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), count);
        } catch (Exception e) {
            LOGGER.error("统计文章评论数-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }
}
