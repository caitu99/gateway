package com.caitu99.gateway.apiconfig.dto.req;

import java.util.List;

/**
 * 分页与排序
 *
 * @author: chenyun
 * @since: 2015年4月29日 下午6:39:55
 * @history:
 */
public class ReqQueryBase<T> {

    private int start = 0;
    private int limit = 20;
    private int total;//记录总数
    private int pages;//总页数
    private String orderColumn;// 排序字段
    private String orderDir;// 排序方向
    private List<T> items;// 存储查询结果
    private boolean requireTotal = true;//是否需要总计数

    public boolean isRequireTotal() {
        return requireTotal;
    }

    public void setRequireTotal(boolean requireTotal) {
        this.requireTotal = requireTotal;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        this.pages = (total + limit - 1) / limit;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public String getOrderDir() {
        return orderDir;
    }

    public void setOrderDir(String orderDir) {
        this.orderDir = orderDir;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }


}
