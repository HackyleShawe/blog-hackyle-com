package com.hackyle.blog.common.constant;

/**
 * 配置项枚举（tb_configuration表）
 */
public enum ConfigItemEnum {
    ARTICLE_TOP("ArticleTop", "ArticleTop2IndexPage") //首页文章置顶
    ;

    /** 配置组名 */
    private final String group;
    /** 配置项的Key */
    private final String key;

    ConfigItemEnum(String group, String key) {
        this.group = group;
        this.key = key;
    }

    public String getGroup() {
        return group;
    }

    public String getKey() {
        return key;
    }

}
