package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.consumer.common.exception.NotFoundException;
import com.hackyle.blog.consumer.dto.ArticleAccessLogDto;
import com.hackyle.blog.consumer.service.LoggerService;
import com.hackyle.blog.consumer.service.ArticleService;
import com.hackyle.blog.consumer.service.CommentService;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.consumer.vo.CommentVo;
import com.hackyle.blog.consumer.vo.MetaVo;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.IDUtils;
import com.hackyle.blog.common.util.IpUtils;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/article")
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;

    @Autowired
    private LoggerService loggerService;

    /**
     * 分页获取所有文章
     */
    @RequestMapping(value = {"/page/{pageNum}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView pageByNum(ModelAndView modelAndView, @PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        PageRequestDto<String> pageRequestDto = new PageRequestDto<>();

        //纠正不合法数据
        if(pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        pageRequestDto.setCurrentPage(pageNum);

        String pageSize = request.getParameter("pageSize");
        if(StringUtils.isBlank(pageSize)) {
            pageRequestDto.setPageSize(15);
        } else {
            pageRequestDto.setPageSize(Integer.parseInt(pageSize));
        }

        //多个关键字，使用逗号分割
        String queryKeywords = request.getParameter("query");
        if(StringUtils.isNotBlank(queryKeywords)) {
            pageRequestDto.setCondition(queryKeywords);
            modelAndView.addObject("queryKeywords", queryKeywords);
        }

        PageResponseDto<ArticleVo> pageResponseDto = articleService.pageByNum(pageRequestDto);

        //文章的Content打日志太大了，使用URI替换
        String articleUris = pageResponseDto.getRows().stream().map(ArticleVo::getUri).collect(Collectors.joining(","));
        LOGGER.info("分页获取所有文章-pageRequestDto={}-articleUris={}", JSON.toJSONString(pageRequestDto), articleUris);

        modelAndView.addObject("pageResponseDto", pageResponseDto);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    /**
     * 文章详情
     */
    @RequestMapping(value = {"/{code}/{link}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView articleDetail(ModelAndView modelAndView, HttpServletRequest request,
                                      @PathVariable("code") String code, @PathVariable("link") String link) {
        long start = System.currentTimeMillis();

        if(StringUtils.isBlank(code)) {
            throw new NotFoundException();
        }

        String uri = "/" + code;
        uri = StringUtils.isNotBlank(link) ? uri + "/" +link : uri;

        ArticleVo articleVo = articleService.articleDetail(uri);

        MetaVo metaVo = new MetaVo();
        metaVo.setTitle(articleVo.getTitle());
        metaVo.setDescription(articleVo.getSummary());
        metaVo.setKeywords(articleVo.getKeywords());

        modelAndView.addObject("articleVo", articleVo);
        modelAndView.addObject("metaVo", metaVo);

        List<CommentVo> commentVos = commentService.fetchListByHierarchy(IDUtils.decrypt(articleVo.getId()));
        modelAndView.addObject("commentVos", commentVos);

        ArticleVo articleVoLog = BeanCopyUtils.copy(articleVo, ArticleVo.class);
        articleVoLog.setContent("文章内容长度：" + articleVoLog.getContent().length());
        LOGGER.info("获取文章详情-controller层入参：{}-controller层出参-articleVo={}, commentVos={}", uri,
                JSON.toJSONString(articleVoLog), JSON.toJSONString(commentVos));

        modelAndView.setViewName("article");

        //记录日志
        ArticleAccessLogDto logDto = new ArticleAccessLogDto();
        logDto.setArticleUri(uri);
        logDto.setIp(IpUtils.getPublicIpv4(request));
        logDto.setTimeUse((int) (System.currentTimeMillis() - start));
        loggerService.insertArticleAccessLog(logDto);

        return modelAndView;
    }

    /**
     * 如果文章没有category则走这里
     */
    @RequestMapping(value = {"/{uri}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView articleDetail(ModelAndView modelAndView, HttpServletRequest request,
                                      @PathVariable("uri") String uri) {
        long start = System.currentTimeMillis();

        if(StringUtils.isBlank(uri)) {
            throw new NotFoundException();
        }

        uri = "/" + uri;

        ArticleVo articleVo = articleService.articleDetail(uri);
        modelAndView.addObject("articleVo", articleVo);

        MetaVo metaVo = new MetaVo();
        metaVo.setTitle(articleVo.getTitle());
        metaVo.setDescription(articleVo.getSummary());
        metaVo.setKeywords(articleVo.getTags());
        modelAndView.addObject("metaVo", metaVo);

        List<CommentVo> commentVos = commentService.fetchListByHierarchy(IDUtils.decrypt(articleVo.getId()));
        modelAndView.addObject("commentVos", commentVos);

        ArticleVo articleVoLog = BeanCopyUtils.copy(articleVo, ArticleVo.class);
        articleVoLog.setContent("文章内容长度：" + articleVoLog.getContent().length());
        LOGGER.info("获取文章详情-controller层入参：{}-controller层出参：articleVo={}, commentVos={}", uri,
                JSON.toJSONString(articleVoLog), JSON.toJSONString(commentVos));

        modelAndView.setViewName("article");

        //记录日志
        ArticleAccessLogDto logDto = new ArticleAccessLogDto();
        logDto.setArticleUri(uri);
        logDto.setIp(IpUtils.getPublicIpv4(request));
        logDto.setTimeUse((int) (System.currentTimeMillis() - start));
        loggerService.insertArticleAccessLog(logDto);

        return modelAndView;
    }


}
