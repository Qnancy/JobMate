package cn.edu.zju.cs.jobmate.dto.admin;

import lombok.Builder;
import lombok.Data;

/**
 * Parsed activity fields for the admin form.
 */
@Data
@Builder
public class ParsedActivityPromotionDto {

    private Long companyId;
    private String companyName;
    private String title;
    /**
     * Local time string compatible with app JSON: yyyy-MM-dd HH:mm:ss
     */
    private String time;
    /**
     * One of LECTURE / JOB_FAIR / COMPANY_VISIT (string form for JSON friendliness).
     * Falls back to LECTURE when the model output is missing or unrecognised.
     */
    private String type;
    private String link;
    private String location;
    private String extra;
}
