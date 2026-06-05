package cn.edu.zju.cs.jobmate.dto.admin;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

/**
 * Admin-confirmed rows to import.
 */
@Data
@Builder
public class SurveyImportConfirmRequest {

    private String fileName;

    @NotEmpty(message = "请至少选择一行导入")
    @Valid
    private List<SurveyImportRowPayload> rows;
}
