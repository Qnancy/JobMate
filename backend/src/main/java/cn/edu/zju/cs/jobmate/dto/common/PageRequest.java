package cn.edu.zju.cs.jobmate.dto.common;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.Max;

/**
 * Pagination request DTO.
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class PageRequest {

    @Builder.Default
    @Min(value = 1, message = "Page number cannot be less than 1")
    private Integer page = 1;

    @Builder.Default
    @Min(value = 1, message = "Page size cannot be less than 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer pageSize = 10;
}
