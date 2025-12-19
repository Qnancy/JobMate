package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.activity.*;
import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * ActivityInfo REST Controller.
 */
@Slf4j
@RestController
@RequestMapping("/api/activities")
@Validated
public class ActivityInfoController {

    private final ActivityInfoService activityInfoService;

    public ActivityInfoController(ActivityInfoService activityInfoService) {
        this.activityInfoService = activityInfoService;
    }

    /**
     * Creates a new ActivityInfo.
     * 
     * @apiNote POST /api/activities
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityInfoResponse>> create(
        @Valid @RequestBody ActivityInfoCreateRequest request
    ) {
        log.info("Creating ActivityInfo with {}", request);
        ActivityInfo activityInfo = activityInfoService.create(request);
        ActivityInfoResponse response = ActivityInfoResponse.from(activityInfo);
        log.info("Successfully created ActivityInfo(id={})", activityInfo.getId());
        return ResponseEntity.ok(ApiResponse.ok("创建成功", response));
    }

    /**
     * Delete ActivityInfo.
     * 
     * @apiNote DELETE /api/activities/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Deleting ActivityInfo(id={})", id);
        activityInfoService.delete(id);
        log.info("Successfully deleted ActivityInfo(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

    /**
     * Update ActivityInfo.
     * 
     * @apiNote PUT /api/activities/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityInfoResponse>> update(
        @PathVariable @NotNull @Positive Integer id,
        @Valid @RequestBody ActivityInfoUpdateRequest request
    ) {
        log.info("Updating ActivityInfo(id={}) with {}", id, request);
        ActivityInfo activityInfo = activityInfoService.update(id, request);
        ActivityInfoResponse response = ActivityInfoResponse.from(activityInfo);
        log.info("Successfully updated ActivityInfo(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
    }

    /**
     * Retrieves an ActivityInfo.
     * 
     * @apiNote GET /api/activities/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityInfoResponse>> get(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Retrieving ActivityInfo(id={})", id);
        ActivityInfo activityInfo = activityInfoService.getById(id);
        ActivityInfoResponse response = ActivityInfoResponse.from(activityInfo);
        log.info("Successfully retrieved ActivityInfo(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Retrieves ActivityInfos with pagination.
     * 
     * @apiNote GET /api/activities
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ActivityInfoResponse>>> getAll(
        @Valid PageRequest request
    ) {
        log.info("Retrieving ActivityInfos with {}", request);

        // Fetch results.
        Page<ActivityInfo> results = activityInfoService.getAll(request);

        // Fetch results -> response DTOs -> final response.
        Page<ActivityInfoResponse> dtos = results.map(ActivityInfoResponse::from);
        PageResponse<ActivityInfoResponse> response = PageResponse.from(dtos);

        log.info("Successfully retrieved {} ActivityInfos", response.getCount());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Search ActivityInfos with query conditions.
     * 
     * @apiNote GET /api/activities/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ActivityInfoResponse>>> search(
        @Valid ActivityInfoQueryRequest request
    ) {
        log.info("Searching ActivityInfos with {}", request);

        // Fetch results.
        Page<ActivityInfo> results = activityInfoService.query(request);

        // Fetch results -> response DTOs -> final response.
        Page<ActivityInfoResponse> dtos = results.map(ActivityInfoResponse::from);
        PageResponse<ActivityInfoResponse> response = PageResponse.from(dtos);

        log.info("Successfully searched {} ActivityInfos", response.getCount());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }
}
