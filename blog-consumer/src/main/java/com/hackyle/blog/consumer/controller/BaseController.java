package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.consumer.dto.PageResponseDto;
import com.hackyle.blog.consumer.service.ArticleService;
import com.hackyle.blog.consumer.vo.ArticleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 放置通用地址映射器
 */
@Controller
public class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = {"/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(ModelAndView modelAndView) {

        PageResponseDto<ArticleVo> pageResponseDto = articleService.pageByNum(1);
        LOGGER.info("首页获取所有文章-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

        modelAndView.addObject("pageResponseDto", pageResponseDto);

        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping(value = {"/about"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView about(ModelAndView modelAndView) {
        modelAndView.setViewName("about");
        return modelAndView;
    }
}
