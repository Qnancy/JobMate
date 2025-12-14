package cn.edu.zju.cs.jobmate.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Pagination response DTO.
 * 
 * @param <T> List element type
 */
public class PageResponse<T> {
    
    private List<T> content; // Data items.
    private Long total; // All data count.
    private Integer count; // Current page data count.
    private Integer page; // Current page number.

    @JsonProperty("page_size")
    private Integer pageSize; // Page size.

    @JsonProperty("total_pages")
    private Integer totalPages; // Total page count.

    public PageResponse() {
    }

    public PageResponse(
        List<T> content,
        Long total,
        Integer count,
        Integer page,
        Integer pageSize
    ) {
        this.content = content;
        this.total = total;
        this.count = count;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    /**
     * Create PageResponse from {@link Page} object.
     * 
     * @param <T> List element type
     * @param page Spring Data Page object
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getTotalElements(),
            page.getNumberOfElements(),
            page.getNumber() + 1, // Convert to 1-based page index.
            page.getSize()
        );
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
        // Recalculate total pages
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
        // Recalculate total pages
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
                ", count=" + count +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                '}';
    }
}
