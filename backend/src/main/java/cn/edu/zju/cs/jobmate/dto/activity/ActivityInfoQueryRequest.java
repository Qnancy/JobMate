package cn.edu.zju.cs.jobmate.dto.activity;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * ActivityInfo pagination query request DTO.
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ActivityInfoQueryRequest extends PageRequest {

    /**
     * Search keyword (searches in title, company_name, title, location).
     */
    private String keyword;

    @Override
    public String toString() {
        return "ActivityInfoQueryRequest{" +
                "page=" + getPage() +
                ", pageSize=" + getPageSize() +
                ", keyword=" + ToStringUtil.wrap(keyword) +
                "}";
    }
}
