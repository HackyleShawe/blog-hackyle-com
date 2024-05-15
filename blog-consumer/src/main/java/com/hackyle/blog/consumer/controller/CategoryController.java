package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.consumer.qo.CategoryQo;
import com.hackyle.blog.consumer.service.ArticleCategoryService;
import com.hackyle.blog.consumer.vo.ArticleVo;
import com.hackyle.blog.consumer.vo.CategoryVo;
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

/**
 * 分类：读取所有的文章分类，渲染到页面category.html。
 * 点击分类，获取该目录下的所有文章，category/分类名/页码
 */
@Controller
public class CategoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private ArticleCategoryService articleCategoryService;

    /**
     * 查询文章分类
     */
    @RequestMapping(value = {"/category"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView queryCategory(ModelAndView modelAndView) {
        //直接获取全部有文章的分类就行了，不需要根据关键字查文章分类
        //String categoryKeys = request.getParameter("categoryKeys");
        //LOGGER.info("查询文章分类-controller层入参-queryKeywords={}", categoryKeys);
        //if(StringUtils.isNotBlank(categoryKeys)) {
        //    modelAndView.addObject("categoryKeys", categoryKeys);
        //}

        List<CategoryVo> categoryVoList = articleCategoryService.selectCategory();

        modelAndView.addObject("categoryVoList", categoryVoList);
        LOGGER.info("查询文章分类-controller层出参-categoryVoList={}", JSON.toJSONString(categoryVoList));

        modelAndView.setViewName("category");
        return modelAndView;
    }

    /**
     * 分页获取某分类下的所有文章
     */
    @RequestMapping(value = {"/category/{categoryCode}/{pageNum}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView categoryArticle(ModelAndView modelAndView, @PathVariable("categoryCode") String categoryCode,
                                        @PathVariable("pageNum") Integer pageNum, HttpServletRequest request) {
        PageRequestDto<CategoryQo> pageRequestDto = new PageRequestDto<>();
        CategoryQo categoryQo = new CategoryQo();
        categoryQo.setCategoryCode(categoryCode);

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
        String categoryKeys = request.getParameter("categoryKeys");
        if(StringUtils.isNotBlank(categoryKeys)) {
            categoryQo.setQueryKeywords(categoryKeys);
            modelAndView.addObject("categoryKeys", categoryKeys);
        }
        pageRequestDto.setCondition(categoryQo);

        LOGGER.info("分页获取某分类下的所有文章-controller入参-pageRequestDto={}", JSON.toJSONString(pageRequestDto));
        PageResponseDto<ArticleVo> pageResponseDto = articleCategoryService.selectArticleByCategory(pageRequestDto);
        String articleUris = pageResponseDto.getRows().stream().map(ArticleVo::getUri).collect(Collectors.joining(","));
        LOGGER.info("分页获取某分类下的所有文章-controller出参-pageRequestDto={}-articleUris={}", JSON.toJSONString(pageRequestDto), articleUris);

        modelAndView.addObject("pageResponseDto", pageResponseDto);
        modelAndView.addObject("categoryCode", categoryCode);
        modelAndView.addObject("categoryName", pageRequestDto.getCondition().getCategoryName());

        modelAndView.setViewName("category");
        return modelAndView;
    }
}
