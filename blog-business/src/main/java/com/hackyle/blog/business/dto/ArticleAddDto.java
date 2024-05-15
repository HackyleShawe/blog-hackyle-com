package com.hackyle.blog.business.dto;

public class ArticleAddDto {
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 总结概要
     */
    private String summary;

    /**
     * 文章关键字，直接用于meta标签，SEO
     */
    private String keywords;

    /**
     * 文章的URI，文章链接最终为：https://domain.com/category-code/URI
     */
    private String uri;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 作者
     */
    private String authorIds;
    /**
     * 分类
     */
    private String categoryIds;
    /**
     * 标签
     */
    private String tagIds;

    /**
     * 封面图URL
     */
    private String faceImgLink;
    /**
     * 是否允许被评论
     */
    private Boolean commented;
    /**
     * 是否允许发布
     */
    private Boolean released;
    /**
     * 是否保存为新版本
     */
    private Boolean newVersion;

    /** 是否删除 */
    private Boolean deleted;

    /** 是否置顶 */
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

    public String getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(String authorIds) {
        this.authorIds = authorIds;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public String getFaceImgLink() {
        return faceImgLink;
    }

    public void setFaceImgLink(String faceImgLink) {
        this.faceImgLink = faceImgLink;
    }

    public Boolean getCommented() {
        return commented;
    }

    public void setCommented(Boolean commented) {
        this.commented = commented;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }

    public Boolean getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(Boolean newVersion) {
        this.newVersion = newVersion;
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
