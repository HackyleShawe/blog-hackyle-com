package com.hackyle.blog.consumer.vo;

import java.util.Date;

/**
 * Article View Object
 */
public class ArticleVo {
    /**
     * 经过加密后的ID
     */
    private String id;

    /**
     * 标题
     */
    private String title;
    /**
     * 总结概要
     */
    private String summary;

    private String uri;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章的作者信息
     */
    private String authors;
    /**
     * 文章的分类
     */
    private String categories;
    /**
     * 文章的标签
      */
    private String tags;
    /**
     * 封面图URL
     */
    private String faceImgLink;
    /**
     * 文章状态：0-草稿 1-发布
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getFaceImgLink() {
        return faceImgLink;
    }

    public void setFaceImgLink(String faceImgLink) {
        this.faceImgLink = faceImgLink;
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
}
