package cn.edu.zju.cs.jobmate.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应类
 * 
 * @param <T> 列表元素类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    
    private List<T> content;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;

    public PageResponse() {
    }

    public PageResponse(List<T> content, Long total, Integer page, Integer pageSize) {
        this.content = content;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    /**
     * 从Spring Data Page对象创建PageResponse
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
        // 重新计算总页数
        if (this.pageSize != null && this.pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) total / this.pageSize);
        }
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
        // 重新计算总页数
        if (this.total != null && pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) this.total / pageSize);
        }
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "content=" + content +
                ", total=" + total +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                '}';
    }
}

