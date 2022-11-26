package com.hackyle.blog.business.qo;

import java.util.Date;

public class ArticleAccessLogQo {
    /**
     * 文章地址，tb_article中的uri字段
     */
    private String articleUri;

    /**
     * 留言者IP（IPv4最多15个字符; IPv6-最多29个字符）
     */
    private String ip;

    /**
     * 地址
     */
    private String address;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 请求耗时
     */
    private Integer timeUse;

    /**
     * 起始时间: 年-月-日 时:分:秒
     */
    private Date startTime;

    /**
     * 结束时间: 年-月-日 时:分:秒
     */
    private Date endTime;

    private Date[] timePicker;

    public String getArticleUri() {
        return articleUri;
    }

    public void setArticleUri(String articleUri) {
        this.articleUri = articleUri;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Integer getTimeUse() {
        return timeUse;
    }

    public void setTimeUse(Integer timeUse) {
        this.timeUse = timeUse;
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

    public Date[] getTimePicker() {
        return timePicker;
    }

    public void setTimePicker(Date[] timePicker) {
        this.timePicker = timePicker;
    }
}
