package com.hackyle.blog.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章访问日志
 */
@TableName(value ="tl_article_access")
public class ArticleAccessEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 创建时间: 年-月-日 时:分:秒
     */
    private Date createTime;

    /**
     * 是否删除：0-false-未删除;1-true-已删除
     */
    @TableField("is_deleted")
    private Boolean deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
