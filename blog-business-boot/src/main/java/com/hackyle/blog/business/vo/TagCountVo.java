package com.hackyle.blog.business.vo;

import java.util.List;

public class TagCountVo {
    private List<String> tagNameList;
    private List<Integer> articleCountList;

    public List<String> getTagNameList() {
        return tagNameList;
    }

    public void setTagNameList(List<String> tagNameList) {
        this.tagNameList = tagNameList;
    }

    public List<Integer> getArticleCountList() {
        return articleCountList;
    }

    public void setArticleCountList(List<Integer> articleCountList) {
        this.articleCountList = articleCountList;
    }
}
