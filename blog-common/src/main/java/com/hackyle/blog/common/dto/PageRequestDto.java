package com.hackyle.blog.common.dto;

import java.io.Serializable;
import java.util.List;

public class PageRequestDto<T> implements Serializable {
    /** 页码 */
    private int currentPage;
    /** 每页记录数 */
    private int pageSize;
    /** 查询条件 */
    private T condition;
    /** 排序条件 */
    private List<OrderItemDto> orders;

    public PageRequestDto() {
        //默认情况下显示第一页，每页显示10条记录
        this.currentPage = 1;
        this.pageSize = 10;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public T getCondition() {
        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }

    public List<OrderItemDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItemDto> orders) {
        this.orders = orders;
    }
}
