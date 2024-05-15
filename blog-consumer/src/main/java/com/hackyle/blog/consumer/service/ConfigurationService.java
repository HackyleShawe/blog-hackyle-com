package com.hackyle.blog.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hackyle.blog.common.constant.ConfigItemEnum;
import com.hackyle.blog.consumer.entity.ConfigurationEntity;

import java.util.List;

public interface ConfigurationService extends IService<ConfigurationEntity> {

    List<ConfigurationEntity> queryConfigByGroup(ConfigItemEnum configItemEnum);

    ConfigurationEntity queryConfigByKey(ConfigItemEnum configItemEnum);

}
