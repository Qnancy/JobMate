package cn.edu.zju.cs.jobmate.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.zju.cs.jobmate.dto.admin.PromotionParseRequest;
import cn.edu.zju.cs.jobmate.dto.admin.PromotionParseResponse;
import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.services.PromotionParseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Admin-only API: parse pasted promotion text with an LLM (does not persist).
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/promotion")
public class AdminPromotionParseController {

    private final PromotionParseService promotionParseService;

    /**
     * Parse unstructured text into job or activity fields.
     *
     * @apiNote POST /api/admin/promotion/parse
     */
    @PostMapping("/parse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PromotionParseResponse>> parse(
        @Valid @RequestBody PromotionParseRequest request
    ) {
        log.info("Admin promotion parse, target={}", request.getTarget());
        PromotionParseResponse data = promotionParseService.parse(request);
        return ResponseEntity.ok(ApiResponse.ok("解析成功", data));
    }
}
