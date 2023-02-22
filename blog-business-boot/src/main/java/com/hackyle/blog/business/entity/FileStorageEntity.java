package com.hackyle.blog.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 静态资源（图片、视频、音乐、文档等）管理
 */
@TableName(value ="tb_file_storage")
public class FileStorageEntity {
    /**
     * ID：为了后续数据迁移，不使用自增主键，使用时间戳
     */
    @TableId
    private Long id;

    private String articleUri;

    private String fileLink;

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

    //@TableField("is_released")
    //private Boolean released;


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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
