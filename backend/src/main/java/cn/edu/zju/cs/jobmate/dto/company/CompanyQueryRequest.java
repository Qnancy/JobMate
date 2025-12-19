package cn.edu.zju.cs.jobmate.dto.company;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Company pagination query request DTO.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompanyQueryRequest extends PageRequest {

    /**
     * Search keyword (searches in name).
     */
    private String keyword;

    /**
     * Company type filter.
     */
    private CompanyType type;

    @Override
    public String toString() {
        return "CompanyQueryRequest{" +
                "page=" + getPage() +
                ", pageSize=" + getPageSize() +
                ", keyword=" + ToStringUtil.wrap(keyword) +
                ", type=" + type +
                "}";
    }
}
