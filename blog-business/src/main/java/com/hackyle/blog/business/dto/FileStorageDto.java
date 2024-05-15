package com.hackyle.blog.business.dto;

public class FileStorageDto {
    private Long id;

    private String articleUri;

    /** e.g. https://res.hackyle.com/blog/2023/02/xxx.png*/
    private String fileLink;

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
}
