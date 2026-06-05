package cn.edu.zju.cs.jobmate.dto.admin;

import lombok.Builder;
import lombok.Data;

/**
 * One parsed CSV row shown to admin before import.
 */
@Data
@Builder
public class SurveyImportPreviewRow {

    private int rowNumber;
    private String rowLabel;
    /** Whether this row can be imported. */
    private boolean valid;
    private String infoType;
    private String companyName;
    private String companyType;
    private String summary;
    /** Planned DB action, e.g. COMPANY_WILL_CREATE. */
    private String plannedAction;
    private String errorMessage;
    /** Payload echoed back on confirm-import. */
    private SurveyImportRowPayload payload;

    /** True when import would change existing company or job data. */
    private boolean hasConflict;

    private SurveyImportExistingCompany existingCompany;

    private SurveyImportExistingJob existingJob;
}
