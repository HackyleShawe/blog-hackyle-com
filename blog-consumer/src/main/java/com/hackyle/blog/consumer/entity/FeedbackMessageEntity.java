package com.hackyle.blog.consumer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 留言和反馈
 */
@TableName(value ="tb_feedback_message")
public class FeedbackMessageEntity implements Serializable {
    /**
     * ID：为了后续数据迁移，不使用自增主键，使用时间戳
     */
    @TableId
    private Long id;

    /**
     * 留言者称呼
     */
    private String name;

    /**
     * 留言者邮箱
     */
    private String email;

    /**
     * 留言者电话
     */
    private String phone;

    /**
     * 留言者的链接（个人主页、博客页）
     */
    private String link;

    /**
     * 留言者IP（IPv4最多15个字符; IPv6-最多29个字符）
     */
    private String ip;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 是否展示（已审查）：0-不展示 1-展示
     */
    @TableField("is_released")
    private Boolean released;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
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
