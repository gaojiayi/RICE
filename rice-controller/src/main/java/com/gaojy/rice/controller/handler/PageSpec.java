package com.gaojy.rice.controller.handler;

/**
 * @author gaojy
 * @Description 分页
 * @createTime 2022/11/20 21:57:00
 */
public class PageSpec {

    private Integer pageIndex;
    private Integer pageSize;
    private Integer total;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public PageSpec(Integer pageIndex, Integer pageSize, Integer total) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.total = total;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
