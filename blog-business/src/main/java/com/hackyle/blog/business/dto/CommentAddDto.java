package com.hackyle.blog.business.dto;

public class CommentAddDto {
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

    /** 是否发布 */
    private Boolean released;

    /**
     * 是否删除：0-false-未删除;1-true-已删除
     */
    private Boolean deleted;

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
}
