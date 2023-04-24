package com.hackyle.blog.business.qo;

public class ConfigurationQo {
    private Long id;

    /**
     * 配置组，用于标识某一类型的配置项
     */
    private String configGroup;

    /**
     * 名字
     */
    private String name;

    /**
     * 配置Key
     */
    private String configKey;

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

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
