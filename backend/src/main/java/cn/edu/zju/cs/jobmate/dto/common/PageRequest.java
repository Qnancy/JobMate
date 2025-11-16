package cn.edu.zju.cs.jobmate.dto.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Pagination request parameters
 */
public class PageRequest {
    
    @Min(value = 0, message = "Page number cannot be less than 0")
    private Integer page = 0;
    
    @Min(value = 1, message = "Page size cannot be less than 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
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

