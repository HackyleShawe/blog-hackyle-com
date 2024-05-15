package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.consumer.qo.TagQo;
import com.hackyle.blog.consumer.service.ArticleTagService;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.consumer.vo.TagVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签：读取标签，渲染tag.html。
 * 点击tag，获取该tag的所有文章，tag/tag名/页码
 */
@RestController
public class TagController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private ArticleTagService articleTagService;


    /**
     * 查询所有文章标签
     */
    @RequestMapping(value = {"/tag"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView queryTag(ModelAndView modelAndView) {
        List<TagVo> tagVoList = articleTagService.selectTag();

        modelAndView.addObject("tagVoList", tagVoList);
        LOGGER.info("查询所有文章标签-controller层出参-tagVoList={}", JSON.toJSONString(tagVoList));

        modelAndView.setViewName("tag");
        return modelAndView;
    }

    /**
     * 分页获取某标签下的所有文章
     */
    @RequestMapping(value = {"/tag/{tagCode}/{pageNum}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView tagArticle(ModelAndView modelAndView, @PathVariable("tagCode") String tagCode,
                                        @PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        PageRequestDto<TagQo> pageRequestDto = new PageRequestDto<>();
        TagQo tagQo = new TagQo();
        tagQo.setTagCode(tagCode);

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
        String tagKeys = request.getParameter("tagKeys");
        if(StringUtils.isNotBlank(tagKeys)) {
            tagQo.setTagKeys(tagKeys);
            modelAndView.addObject("tagKeys", tagKeys);
        }
        pageRequestDto.setCondition(tagQo);

        LOGGER.info("分页获取某标签下的所有文章-controller入参-pageRequestDto={}", JSON.toJSONString(pageRequestDto));
        PageResponseDto<ArticleVo> pageResponseDto = articleTagService.selectArticleByTag(pageRequestDto);
        String articleUris = pageResponseDto.getRows().stream().map(ArticleVo::getUri).collect(Collectors.joining(","));
        LOGGER.info("分页获取某标签下的所有文章-controller出参-pageRequestDto={}-articleUris={}", JSON.toJSONString(pageRequestDto), articleUris);

        modelAndView.addObject("pageResponseDto", pageResponseDto);
        modelAndView.addObject("tagCode", tagCode);
        modelAndView.addObject("tagName", pageRequestDto.getCondition().getTagName());

        modelAndView.setViewName("tag");
        return modelAndView;
    }

}
