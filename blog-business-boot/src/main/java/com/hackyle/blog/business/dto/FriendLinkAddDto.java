package com.hackyle.blog.business.dto;

public class FriendLinkAddDto {
    private Long id;

    /**
     * 友链名称
     */
    private String name;

    /**
     * 链接
     */
    private String linkUrl;

    /**
     * 链接描述
     */
    private String description;

    /**
     * 友链图片地址
     */
    private String linkAvatarUrl;

    /**
     * 链接排序权重
     */
    private Integer rankWeight;

    private Boolean deleted;

    private Boolean released;

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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkAvatarUrl() {
        return linkAvatarUrl;
    }

    public void setLinkAvatarUrl(String linkAvatarUrl) {
        this.linkAvatarUrl = linkAvatarUrl;
    }

    public Integer getRankWeight() {
        return rankWeight;
    }

    public void setRankWeight(Integer rankWeight) {
        this.rankWeight = rankWeight;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }
}
