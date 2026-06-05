package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportConfirmRequest;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportPreviewResponse;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportResponse;
import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.services.SurveyCsvImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin-only API: preview and import Jinshuju employer survey CSV.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/survey")
public class AdminSurveyImportController {

    private final SurveyCsvImportService surveyCsvImportService;

    /**
     * Parse CSV and return preview rows for admin confirmation.
     *
     * @apiNote POST /api/admin/survey/preview
     */
    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SurveyImportPreviewResponse>> preview(
        @RequestParam("file") MultipartFile file
    ) {
        log.info("Admin survey CSV preview, file={}", file != null ? file.getOriginalFilename() : null);
        SurveyImportPreviewResponse data = surveyCsvImportService.previewCsv(file);
        return ResponseEntity.ok(ApiResponse.ok("解析成功", data));
    }

    /**
     * Import admin-confirmed rows.
     *
     * @apiNote POST /api/admin/survey/import
     */
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SurveyImportResponse>> importRows(
        @Valid @RequestBody SurveyImportConfirmRequest request
    ) {
        log.info("Admin survey CSV import confirmed, rows={}",
            request.getRows() != null ? request.getRows().size() : 0);
        SurveyImportResponse data = surveyCsvImportService.importRows(request);
        return ResponseEntity.ok(ApiResponse.ok("导入完成", data));
    }
}
