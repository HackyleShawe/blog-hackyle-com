package com.hackyle.blog.consumer.qo;

public class CategoryQo {
    private String categoryCode;
    private String categoryName;
    private String queryKeywords;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getQueryKeywords() {
        return queryKeywords;
    }

    public void setQueryKeywords(String queryKeywords) {
        this.queryKeywords = queryKeywords;
    }
}
