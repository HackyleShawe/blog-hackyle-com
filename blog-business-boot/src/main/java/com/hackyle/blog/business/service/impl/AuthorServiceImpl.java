package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.AuthorAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.AuthorEntity;
import com.hackyle.blog.business.mapper.AuthorMapper;
import com.hackyle.blog.business.service.AuthorService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.AuthorVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private AuthorMapper authorMapper;

    @Transactional
    @Override
    public ApiResponse<String> add(AuthorAddDto addDto) {
        AuthorEntity authorEntity = BeanCopyUtils.copy(addDto, AuthorEntity.class);

        //nick name存在性检查
        QueryWrapper<AuthorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AuthorEntity::getNickName, addDto.getNickName())
                .eq(AuthorEntity::getDeleted, 0);
        Integer count = authorMapper.selectCount(queryWrapper);
        if(count >= 1) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "Nick Name已存在，请重新的定义");
        }

        authorEntity.setId(IDUtils.timestampID());
        int inserted = authorMapper.insert(authorEntity);
        if(inserted == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "新增作者失败");
        }
    }

    @Transactional
    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(IDUtils.decryptByAES(idStr));
        }
        int deleted = authorMapper.logicDeleteByIds(idList);
        if(deleted == idSplit.length) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "删除作者失败");
        }
    }

    @Transactional
    @Override
    public ApiResponse<String> update(AuthorAddDto updateDto) {
        AuthorEntity authorEntity = BeanCopyUtils.copy(updateDto, AuthorEntity.class);
        authorEntity.setId(IDUtils.decryptByAES(updateDto.getId()));

        //nick name存在性检查
        QueryWrapper<AuthorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AuthorEntity::getNickName, authorEntity.getNickName())
                .eq(AuthorEntity::getDeleted, 0);
        AuthorEntity checkAuthorEntity = authorMapper.selectOne(queryWrapper);
        if(checkAuthorEntity != null && !authorEntity.getId().equals(checkAuthorEntity.getId())) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "Nick Name已存在，请重新的定义");
        }

        UpdateWrapper<AuthorEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(AuthorEntity::getId, authorEntity.getId());
        int updated = authorMapper.update(authorEntity, updateWrapper);

        if(updated == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "更新作者失败");
        }
    }

    /**
     * 分页获取所有文章
     * @param pageRequestDto 查询条件
     * @return 文章
     */
    @Override
    public PageResponseDto<AuthorVo> fetchList(PageRequestDto<String> pageRequestDto) {
        QueryWrapper<AuthorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        //组装查询条件
        //ArticleTagQo articleCategoryQo = pageRequestDto.getCondition();
        //if(articleCategoryQo != null) {
        //    if(StringUtils.isNotBlank(articleCategoryQo.getName())) {
        //        queryWrapper.like("name", articleCategoryQo.getName());
        //    }
        //    if(StringUtils.isNotBlank(articleCategoryQo.getShortName())) {
        //        queryWrapper.like("short_name", articleCategoryQo.getShortName());
        //    }
        //}

        //分页操作
        Page<AuthorEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, AuthorEntity.class);
        Page<AuthorEntity> resultPage = authorMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        PageResponseDto<AuthorVo> authorVoPageResponseDto = PaginationUtils.IPage2PageResponse(resultPage, AuthorVo.class);
        IDUtils.batchEncrypt(resultPage.getRecords(), authorVoPageResponseDto.getRows());

        return authorVoPageResponseDto;
    }

    @Override
    public AuthorVo fetch(String id) {
        long idd = IDUtils.decryptByAES(id);

        AuthorEntity authorEntity = authorMapper.selectById(idd);
        LOGGER.info("获取文章-入参-idd={}-数据库查询结果-article={}", idd, JSON.toJSONString(authorEntity));

        AuthorVo articleVo = new AuthorVo();
        BeanUtils.copyProperties(authorEntity, articleVo);
        articleVo.setId(IDUtils.encryptByAES(authorEntity.getId()));

        return articleVo;
    }

    @Override
    public List<AuthorVo> fetchAll() {
        QueryWrapper<AuthorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        List<AuthorEntity> authorEntityList = authorMapper.selectList(queryWrapper);
        LOGGER.info("获取所有文章分类-查询数据库出参-articleCategoryList={}", JSON.toJSONString(authorEntityList));

        List<AuthorVo> authorVoList = BeanCopyUtils.copyList(authorEntityList, AuthorVo.class);
        IDUtils.batchEncrypt(authorEntityList, authorVoList);

        return authorVoList;
    }
}
