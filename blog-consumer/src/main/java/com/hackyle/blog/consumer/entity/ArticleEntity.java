package com.hackyle.blog.consumer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章主体
 */
@TableName(value ="tb_article")
public class ArticleEntity implements Serializable {
    /**
     * ID：为了后续数据迁移，不使用自增主键，使用时间戳
     */
    @TableId
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
     * 文章的URI，文章链接最终为：https://domain.com/category-code/URI
     */
    private String uri;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章历史版本号
     */
    private Integer version;

    /**
     * 封面图URL
     */
    private String faceImgLink;

    /**
     * 是否发布：0-草稿 1-发布
     */
    @TableField("is_released")
    private Boolean released;

    /**
     * 是否可以评论：0-不可评论 1-可以评论
     */
    @TableField("is_commented")
    private Boolean commented;

    /**
     * 创建时间: 年-月-日 时:分:秒
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}