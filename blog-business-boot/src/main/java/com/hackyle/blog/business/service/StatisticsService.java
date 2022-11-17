package com.hackyle.blog.business.service;

public interface StatisticsService {
    Integer countArticles();

    Integer countCategories();

    Integer countTags();

    Integer countComments();
}
