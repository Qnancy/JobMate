package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportConfirmRequest;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportPreviewResponse;
import cn.edu.zju.cs.jobmate.dto.admin.SurveyImportResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Import employer survey CSV exported from Jinshuju.
 */
public interface SurveyCsvImportService {

    SurveyImportPreviewResponse previewCsv(MultipartFile file);

    SurveyImportResponse importRows(SurveyImportConfirmRequest request);
}
