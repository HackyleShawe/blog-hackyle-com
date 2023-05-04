package com.hackyle.blog.business.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.business.common.constant.ConfigItemEnum;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.ConfigurationAddDto;
import com.hackyle.blog.business.dto.PageRequestDto;
import com.hackyle.blog.business.dto.PageResponseDto;
import com.hackyle.blog.business.entity.ConfigurationEntity;
import com.hackyle.blog.business.mapper.ConfigurationMapper;
import com.hackyle.blog.business.qo.ConfigurationQo;
import com.hackyle.blog.business.service.ConfigurationService;
import com.hackyle.blog.business.util.BeanCopyUtils;
import com.hackyle.blog.business.util.IDUtils;
import com.hackyle.blog.business.util.PaginationUtils;
import com.hackyle.blog.business.vo.ConfigurationVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigurationServiceImpl extends ServiceImpl<ConfigurationMapper, ConfigurationEntity>
    implements ConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Transactional
    @Override
    public ApiResponse<String> add(ConfigurationAddDto addDto) {
        ConfigurationEntity configEntity = BeanCopyUtils.copy(addDto, ConfigurationEntity.class);
        configEntity.setId(IDUtils.timestampID());

        //Key唯一性检验
        QueryWrapper<ConfigurationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConfigurationEntity::getConfigKey, addDto.getConfigKey());
                //.eq(ConfigurationEntity::getDeleted, 0); 在校验Key时，包含已经逻辑删除了的，放置后续撤销逻辑删除后导致唯一性被破坏
        Integer count = configurationMapper.selectCount(queryWrapper);
        if(count >= 1) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "Config Key已存在，请重新的定义");
        }

        int inserted = configurationMapper.insert(configEntity);
        if(inserted == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "新增配置失败");
        }
    }

    @Transactional
    @Override
    public ApiResponse<String> del(String ids) {
        String[] idSplit = ids.split(",");

        List<Long> idList = new ArrayList<>();
        for (String idStr : idSplit) {
            idList.add(Long.parseLong(idStr));
        }

        int deleted = configurationMapper.deleteBatchIds(idList);
        //int deleted = configurationMapper.logicDeleteByIds(idList);
        if(deleted == idSplit.length) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "删除配置失败");
        }
    }

    @Transactional
    @Override
    public ApiResponse<String> update(ConfigurationAddDto updateDto) {
        ConfigurationEntity configEntity = BeanCopyUtils.copy(updateDto, ConfigurationEntity.class);
        configEntity.setId(updateDto.getId());

        //Key唯一性检验
        QueryWrapper<ConfigurationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConfigurationEntity::getConfigKey, updateDto.getConfigKey());
        //.eq(ConfigurationEntity::getDeleted, 0); 在校验Key时，包含已经逻辑删除了的，放置后续撤销逻辑删除后导致唯一性被破坏
        ConfigurationEntity checkEntity = configurationMapper.selectOne(queryWrapper);
        if(checkEntity != null && !checkEntity.getId().equals(updateDto.getId())) {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "Config Key已存在，请重新的定义");
        }

        UpdateWrapper<ConfigurationEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(ConfigurationEntity::getId, configEntity.getId());
        int updated = configurationMapper.update(configEntity, updateWrapper);

        if(updated == 1) {
            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } else {
            return ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), "更新配置失败");
        }
    }

    @Override
    public ConfigurationVo fetch(String id) {
        long idd = IDUtils.decryptByAES(id);

        ConfigurationEntity configEntity = configurationMapper.selectById(idd);
        LOGGER.info("获取文章-入参-idd={}-数据库查询结果-article={}", idd, JSON.toJSONString(configEntity));

        ConfigurationVo articleVo = new ConfigurationVo();
        BeanUtils.copyProperties(configEntity, articleVo);

        return articleVo;
    }

    /**
     * 分页获取所有文章
     * @param pageRequestDto 查询条件
     * @return 文章
     */
    @Override
    public PageResponseDto<ConfigurationVo> fetchList(PageRequestDto<ConfigurationQo> pageRequestDto) {
        QueryWrapper<ConfigurationEntity> queryWrapper = new QueryWrapper<>();

        //组装查询条件
        ConfigurationQo configurationQo = pageRequestDto.getCondition();
        if(configurationQo != null) {
            if(StringUtils.isNotBlank(configurationQo.getGroupName())) {
                queryWrapper.lambda().like(ConfigurationEntity::getGroupName, configurationQo.getGroupName());
            }
            if(StringUtils.isNotBlank(configurationQo.getConfigKey())) {
                queryWrapper.lambda().like(ConfigurationEntity::getConfigKey, configurationQo.getConfigKey());
            }
            if(configurationQo.getDeleted() != null) {
                queryWrapper.lambda().eq(ConfigurationEntity::getDeleted, configurationQo.getDeleted());
            } else {
                queryWrapper.lambda().eq(ConfigurationEntity::getDeleted, Boolean.FALSE);
            }
        }
        queryWrapper.lambda().orderByDesc(ConfigurationEntity::getUpdateTime);

        //分页操作
        Page<ConfigurationEntity> paramPage = PaginationUtils.PageRequest2IPage(pageRequestDto, ConfigurationEntity.class);
        Page<ConfigurationEntity> resultPage = configurationMapper.selectPage(paramPage, queryWrapper);
        LOGGER.info("条件查询分类-入参-pageRequestDto={},出参-resultPage.getRecords()={}",
                JSON.toJSONString(pageRequestDto), JSON.toJSONString(resultPage.getRecords()));

        return PaginationUtils.IPage2PageResponse(resultPage, ConfigurationVo.class);
    }

    @Override
    public void updateConfigById(ConfigurationEntity configurationEntity) {
        if(configurationEntity == null || configurationEntity.getId() == null) {
            return;
        }
        configurationMapper.updateById(configurationEntity);
    }


    /**
     * 查询某个分组下的所有配置项
     */
    @Override
    public List<ConfigurationEntity> queryConfigByGroup(ConfigItemEnum configItemEnum) {
        if(StringUtils.isEmpty(configItemEnum.getGroup())) {
            return new ArrayList<>();
        }

        QueryWrapper<ConfigurationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConfigurationEntity::getGroupName, configItemEnum.getGroup())
                .eq(ConfigurationEntity::getDeleted, 0);
        return configurationMapper.selectList(queryWrapper);
    }

    /**
     * 根据配置项Key查询，Key是唯一的
     */
    @Override
    public ConfigurationEntity queryConfigByKey(ConfigItemEnum configItemEnum) {
        if(StringUtils.isEmpty(configItemEnum.getKey())) {
            return null;
        }

        QueryWrapper<ConfigurationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConfigurationEntity::getConfigKey, configItemEnum.getKey())
                .eq(ConfigurationEntity::getDeleted, 0);
        return configurationMapper.selectOne(queryWrapper);
    }

}

