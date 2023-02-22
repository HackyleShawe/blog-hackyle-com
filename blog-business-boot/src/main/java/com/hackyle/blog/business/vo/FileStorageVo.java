package com.hackyle.blog.business.vo;

import java.util.Date;

public class FileStorageVo {
    private Long id;

    private String articleUri;

    private String fileLink;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArticleUri() {
        return articleUri;
    }

    public void setArticleUri(String articleUri) {
        this.articleUri = articleUri;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
