package com.hackyle.blog.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hackyle.blog.consumer.common.constant.ConfigItemEnum;
import com.hackyle.blog.consumer.entity.ConfigurationEntity;
import com.hackyle.blog.consumer.mapper.ConfigurationMapper;
import com.hackyle.blog.consumer.service.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigurationServiceImpl extends ServiceImpl<ConfigurationMapper, ConfigurationEntity>
    implements ConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    @Autowired
    private ConfigurationMapper configurationMapper;

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

