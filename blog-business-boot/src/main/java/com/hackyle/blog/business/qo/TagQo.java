package com.hackyle.blog.business.qo;

public class TagQo {
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签简称
     */
    private String code;

    /**
     * 标签描述
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}
