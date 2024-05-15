package com.hackyle.blog.business.qo;

import java.util.Date;

/**
 * 文章查询条件
 */
public class ArticleQo {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 总结概要
     */
    private String summary;

    /** 分类ID */
    private Long categoryId;

    /**
     * 文章历史版本号
     */
    private Integer version;

    /**
     * 是否发布：0-草稿 1-发布
     */
    private Boolean released;

    /**
     * 是否可以评论：0-不可评论 1-可以评论
     */
    private Boolean commented;

    /**
     * 更新时间
     */
    private Date updateTime;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }

    public Boolean getCommented() {
        return commented;
    }

    public void setCommented(Boolean commented) {
        this.commented = commented;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
