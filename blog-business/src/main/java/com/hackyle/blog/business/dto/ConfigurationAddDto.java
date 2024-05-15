package com.hackyle.blog.business.dto;

public class ConfigurationAddDto {
    private Long id;

    /**
     * 配置组，用于标识某一类型的配置项
     */
    private String groupName;

    /**
     * 配置组描述
     */
    private String groupDescription;

    /**
     * 配置项Key
     */
    private String configKey;

    /**
     * 配置项Value
     */
    private String configValue;

    /**
     * 配置项Value的拓展信息
     */
    private String configExtend;

    /**
     * 配置项描述
     */
    private String configDescription;

    /**
     * 是否删除：0-false-未删除;1-true-已删除
     */
    private Boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigExtend() {
        return configExtend;
    }

    public void setConfigExtend(String configExtend) {
        this.configExtend = configExtend;
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
