package cn.edu.zju.cs.jobmate.dto;

import java.util.List;

/**
 * 分页响应类
 * 
 * @param <T> 列表元素类型
 */
public class PageResponse<T> {
    
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer pageSize;

    public PageResponse() {
    }

    public PageResponse(List<T> list, Long total, Integer page, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "list=" + list +
                ", total=" + total +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}

