package cn.edu.zju.cs.jobmate.dto.admin;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Result of promotion text parsing for admin UI.
 */
@Data
@Builder
public class PromotionParseResponse {

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    /**
     * When the parsed company name does not match any existing company in DB,
     * this field carries the raw name so that the admin UI can offer a
     * "create this company as well" shortcut. Null when resolved or unknown.
     */
    private String suggestedCompanyName;

    private ParsedJobPromotionDto job;
    private ParsedActivityPromotionDto activity;
}
