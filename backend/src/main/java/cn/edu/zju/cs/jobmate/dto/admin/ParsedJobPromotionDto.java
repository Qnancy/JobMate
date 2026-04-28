package cn.edu.zju.cs.jobmate.dto.admin;

import lombok.Builder;
import lombok.Data;

/**
 * Parsed job fields for the admin form (snake_case in JSON via global naming).
 */
@Data
@Builder
public class ParsedJobPromotionDto {

    private Long companyId;
    private String companyName;
    private String position;
    private String recruitType;
    private String link;
    private String location;
    private String extra;
    /**
     * Optional application deadline as a local date-time string in JsonConfig format
     * (yyyy-MM-dd HH:mm:ss). Empty when not detected.
     */
    private String deadline;

    /** 学历要求枚举名，如 BACHELOR_AND_ABOVE；未识别时可为 null。 */
    private String educationRequirement;
}
