package com.hackyle.blog.business.vo;

import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;
import java.util.List;

public class CommentVo {
    private Long id;

    /**
     * 被评论主体(文章、页面、动态)的ID
     */
    private Long targetId;

    /**
     * 评论者名称
     */
    private String name;

    /**
     * 评论人的邮箱
     */
    private String email;

    /**
     * 评论者的拓展链接，如个人网站地址
     */
    private String link;

    /**
     * 评论时的ip地址
     */
    private String ip;

    /**
     * 被评论者的名称
     */
    private String replyWho;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论，如果为-1表示没有父评论
     */
    private Long parentId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否发布评论：0-未审核，1-审核通过
     */
    private Boolean released;

    /**
     * 是否删除：0-false-未删除;1-true-已删除
     */
    private Boolean deleted;

    /**
     * 子评论、回复
     */
    private List<CommentVo> replyList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
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

    public String getReplyWho() {
        return replyWho;
    }

    public void setReplyWho(String replyWho) {
        this.replyWho = replyWho;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getReleased() {
        return released;
    }

    public void setReleased(Boolean released) {
        this.released = released;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<CommentVo> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<CommentVo> replyList) {
        this.replyList = replyList;
    }
}
