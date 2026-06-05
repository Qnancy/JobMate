package cn.edu.zju.cs.jobmate.dto.admin;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Preview result before admin confirms import.
 */
@Data
@Builder
public class SurveyImportPreviewResponse {

    private String fileName;
    private int totalRows;
    private int validCount;
    private int invalidCount;
    private List<SurveyImportPreviewRow> rows;
}
