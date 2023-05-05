package com.hackyle.blog.business.vo;

import java.util.Date;
import java.util.List;

/**
 * Article View Object
 */
public class ArticleVo {
    private Long id;
    /**
     * 标题
     */
    private String title;

    private String uri;

    /**
     * 总结概要
     */
    private String summary;

    /**
     * 文章关键字，直接用于meta标签，SEO
     */
    private String keywords;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章的作者信息
     */
    private List<AuthorVo> authors;
    /**
     * 文章的分类
     */
    private List<CategoryVo> categories;
    /**
     * 文章的标签
      */
    private List<TagVo> tags;

    /**
     * 文章历史版本号
     */
    private Integer version;
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

    /** 是否删除 */
    private Boolean deleted;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否置顶
     */
    private Boolean toTop;

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<AuthorVo> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorVo> authors) {
        this.authors = authors;
    }

    public List<CategoryVo> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryVo> categories) {
        this.categories = categories;
    }

    public List<TagVo> getTags() {
        return tags;
    }

    public void setTags(List<TagVo> tags) {
        this.tags = tags;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getToTop() {
        return toTop;
    }

    public void setToTop(Boolean toTop) {
        this.toTop = toTop;
    }
}
