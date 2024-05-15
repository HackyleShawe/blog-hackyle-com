package com.hackyle.blog.business.qo;

public class CommentQo {
    private String id;

    /**
     * 被评论主体(文章、页面、动态)的ID
     */
    private Long targetId;

    /**
     * 根据此父评论ID查找其子评论
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者的称呼
     */
    private String name;

    /**
     * 评论者的电邮
     */
    private String email;

    /**
     * 是否发布评论：0-未审核，1-审核通过
     */
    private Boolean released;
    /**
     * 是否删除：0-false-未删除;1-true-已删除
     */
    private Boolean deleted;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
