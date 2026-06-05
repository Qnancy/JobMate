package cn.edu.zju.cs.jobmate.dto.admin;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Summary of a survey CSV import run.
 */
@Data
@Builder
public class SurveyImportResponse {

    private String fileName;
    private int totalRows;
    private int successCount;
    private int failedCount;
    private List<SurveyImportRowResult> rows;
}
