package cn.edu.zju.cs.jobmate.dto.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 分页请求参数
 */
public class PageRequest {
    
    @Min(value = 0, message = "页码不能小于0")
    private Integer page = 0;
    
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    public PageRequest() {
    }

    public PageRequest(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
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
}

