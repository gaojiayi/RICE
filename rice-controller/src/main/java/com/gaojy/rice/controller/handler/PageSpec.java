package com.gaojy.rice.controller.handler;

/**
 * @author gaojy
 * @Description 分页
 * @createTime 2022/11/20 21:57:00
 */
public class PageSpec {

    private Integer pageIndex;
    private Integer pageLimit;
    private Integer totalPage;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(Integer pageLimit) {
        this.pageLimit = pageLimit;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
