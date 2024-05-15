package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.CategoryAddDto;
import com.hackyle.blog.business.entity.CategoryEntity;
import com.hackyle.blog.business.mapper.CategoryMapper;
import com.hackyle.blog.business.qo.CategoryQo;
import com.hackyle.blog.business.service.CategoryService;
import com.hackyle.blog.business.vo.CategoryVo;
import com.hackyle.blog.common.util.BeanCopyUtils;
import com.hackyle.blog.common.util.IDUtils;
import com.hackyle.blog.common.util.PaginationUtils;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ApiResponse<String> add(CategoryAddDto categoryAddDto) {
        CategoryEntity categoryEntity = BeanCopyUtils.copy(categoryAddDto, CategoryEntity.class);
        categoryEntity.setCode(categoryEntity.getCode().toLowerCase());

        //检查是否已经插入：检查code
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CategoryEntity::getCode, categoryAddDto.getCode())
                .eq(CategoryEntity::getDeleted, 0);
        Integer count = categoryMapper.selectCount(queryWrapper);
        if(count >= 1) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "文章分类已存在，请重新定义");
        }

        categoryEntity.setId(IDUtils.timestampID());
        int inserted = categoryMapper.insert(categoryEntity);
        if(inserted == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "新增文章分类失败");
        }
    }

    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(Long.parseLong(idStr));
        }
        int deleted = categoryMapper.logicDeleteByIds(idList);

        if(deleted == idSplit.length) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "删除文章分类失败");
        }

    }

    @Override
    public ApiResponse<String> update(CategoryAddDto categoryAddDto) {
        CategoryEntity categoryEntity = BeanCopyUtils.copy(categoryAddDto, CategoryEntity.class);
        categoryEntity.setCode(categoryEntity.getCode().toLowerCase());

        //Code重复性检查
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CategoryEntity::getCode, categoryEntity.getCode())
                .eq(CategoryEntity::getDeleted, 0);
        CategoryEntity checkCategoryEntity = categoryMapper.selectOne(queryWrapper);
        if(checkCategoryEntity != null && !categoryEntity.getId().equals(checkCategoryEntity.getId())) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "Code已存在，请重新的定义");
        }

        UpdateWrapper<CategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(CategoryEntity::getId, categoryEntity.getId());
        int updated = categoryMapper.update(categoryEntity, updateWrapper);

        if(updated == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "更新文章分类失败");
        }
    }


    @Override
    public CategoryVo fetch(String id) {
        long idd = Long.parseLong(id);

        CategoryEntity categoryEntity = categoryMapper.selectById(idd);
        return BeanCopyUtils.copy(categoryEntity, CategoryVo.class);
    }

    @Override
    public PageResponseDto<CategoryVo> fetchList(PageRequestDto<CategoryQo> pageRequestDto) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        //组装查询条件
        CategoryQo categoryQo = pageRequestDto.getCondition();
        if(categoryQo != null) {
            if(StringUtils.isNotBlank(categoryQo.getName())) {
                queryWrapper.like("name", categoryQo.getName());
            }
            if(StringUtils.isNotBlank(categoryQo.getCode())) {
                queryWrapper.like("code", categoryQo.getCode());
            }
        }
        queryWrapper.lambda().orderByDesc(CategoryEntity::getUpdateTime);

        //分页操作
        Page<CategoryEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, CategoryEntity.class);
        Page<CategoryEntity> resultPage = categoryMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        PageResponseDto<CategoryVo> categoryVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, CategoryVo.class);

        return categoryVoPageResponseDto;
    }

    @Override
    public List<CategoryVo> fetchAll() {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.lambda().orderByDesc(CategoryEntity::getUpdateTime);

        List<CategoryEntity> articleCategoryEntityList = categoryMapper.selectList(queryWrapper);
        LOGGER.info("获取所有文章分类-查询数据库出参-articleCategoryList={}", JSON.toJSONString(articleCategoryEntityList));

        List<CategoryVo> categoryVos = BeanCopyUtils.copyList(articleCategoryEntityList, CategoryVo.class);

        return categoryVos;
    }
}
