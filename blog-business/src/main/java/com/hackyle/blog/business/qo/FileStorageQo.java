package com.hackyle.blog.business.qo;

import java.util.Date;

public class FileStorageQo {
    /**
     * 文章地址，tb_article中的uri字段
     */
    private String articleUri;

    /**
     * 文件地址
     */
    private String fileLink;

    /**
     * fileLink字段有值，但articleUri字段无值
     */
    private Boolean emptyLink;

    /**
     * 起始时间: 年-月-日 时:分:秒
     */
    private Date startTime;
    /**
     * 结束时间: 年-月-日 时:分:秒
     */
    private Date endTime;

    private Date[] timeRange;

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

    public Boolean getEmptyLink() {
        return emptyLink;
    }

    public void setEmptyLink(Boolean emptyLink) {
        this.emptyLink = emptyLink;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date[] getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(Date[] timeRange) {
        this.timeRange = timeRange;
    }
}
