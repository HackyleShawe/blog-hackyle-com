package com.hackyle.blog.business.dto;

public class CategoryAddDto {
    private String id;

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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
