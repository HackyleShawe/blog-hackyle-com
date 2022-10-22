package com.hackyle.blog.business.dto;

import java.io.Serializable;
import java.util.List;

public class PageResponseDto<T> implements Serializable {
    /** 当前页码 */
    private Long currentPage;
    /** 当前页结果 */
    private List<T> rows;

    /** 每页记录数 */
    private Long pageSize;
    /** 总页数 */
    private Long pages;
    /** 总记录数 */
    private Long total;

    public PageResponseDto() {
    }
    public PageResponseDto(List<T> rows, Long total) {
        this.rows = rows;
        this.total = total;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
