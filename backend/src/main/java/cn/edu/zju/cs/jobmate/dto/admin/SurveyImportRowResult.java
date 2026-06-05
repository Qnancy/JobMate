package cn.edu.zju.cs.jobmate.dto.admin;

import lombok.Builder;
import lombok.Data;

/**
 * Per-row result for survey CSV import.
 */
@Data
@Builder
public class SurveyImportRowResult {

    private int rowNumber;
    private String rowLabel;
    private boolean success;
    /** e.g. COMPANY_CREATED, COMPANY_UPDATED, JOB_CREATED, JOB_UPDATED */
    private String action;
    private String message;
    private Long companyId;
    private Long jobId;
}
