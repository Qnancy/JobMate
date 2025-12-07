package cn.edu.zju.cs.jobmate.dto.common;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;

/**
 * Pagination request DTO.
 */
@Data
@SuperBuilder
public class PageRequest {

    @Builder.Default
    @JsonProperty("page")
    @Min(value = 0, message = "Page number cannot be less than 0")
    private Integer page = 0;

    @Builder.Default
    @JsonProperty("page_size")
    @Min(value = 1, message = "Page size cannot be less than 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer pageSize = 10;
}
