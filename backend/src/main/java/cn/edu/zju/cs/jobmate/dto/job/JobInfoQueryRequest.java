package cn.edu.zju.cs.jobmate.dto.job;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * JobInfo pagination query request DTO.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobInfoQueryRequest extends PageRequest {

    /**
     * Search keyword (searches in company_name, position, location).
     */
    private String keyword;

    /**
     * Recruitment type filter.
     */
    private RecruitType recruitType;
}
