package com.hackyle.blog.common.dto;

/**
 * 排序项
 */
public class OrderItemDto {
    /**
     * 列名
     */
    private String column;
    /**
     * 是否升序：true为升序，false为降序
     */
    private boolean asc;

    public OrderItemDto() {
        this.asc = true;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
