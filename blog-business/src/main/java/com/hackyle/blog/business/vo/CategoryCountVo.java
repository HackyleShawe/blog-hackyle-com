package com.hackyle.blog.business.vo;

import java.util.List;

public class CategoryCountVo {
    private List<String> categoryNameList;
    private List<Integer> articleCountList;

    public List<String> getCategoryNameList() {
        return categoryNameList;
    }

    public void setCategoryNameList(List<String> categoryNameList) {
        this.categoryNameList = categoryNameList;
    }

    public List<Integer> getArticleCountList() {
        return articleCountList;
    }

    public void setArticleCountList(List<Integer> articleCountList) {
        this.articleCountList = articleCountList;
    }
}
