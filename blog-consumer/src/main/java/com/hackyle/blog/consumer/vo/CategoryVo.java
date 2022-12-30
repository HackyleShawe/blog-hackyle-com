package com.hackyle.blog.consumer.vo;

import java.util.Date;

public class CategoryVo {
    /**
     * 加密后的ID
     */
    private String id;

    /**
     * 该个categoryId下的文章数量
     */
    private Integer articleNum;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类简称
     */
    private String code;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类的图标URL
     */
    private String iconUrl;

    /**
     * 上一级分类
     */
    private String parentId;

    /**
     * 更新时间
     */
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(Integer articleNum) {
        this.articleNum = articleNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
