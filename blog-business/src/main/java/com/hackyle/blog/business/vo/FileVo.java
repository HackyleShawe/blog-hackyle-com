package com.hackyle.blog.business.vo;

import java.util.Date;

public class FileVo {
    private String url;

    private Date uploadTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }
}
