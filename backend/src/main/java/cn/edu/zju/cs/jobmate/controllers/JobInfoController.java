package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.common.*;
import cn.edu.zju.cs.jobmate.dto.job.*;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.services.JobInfoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * JobInfo REST Controller.
 */
@Slf4j
@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobInfoController {

    private final JobInfoService jobInfoService;

    public JobInfoController(JobInfoService jobInfoService) {
        this.jobInfoService = jobInfoService;
    }

    /**
     * Create a new JobInfo.
     * 
     * @apiNote POST /api/jobs
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JobInfoResponse>> createJobInfo(
        @Valid @RequestBody JobInfoCreateRequest request
    ) {
        log.info("Creating JobInfo with {}", request);
        JobInfo jobInfo = jobInfoService.create(request);
        JobInfoResponse response = JobInfoResponse.from(jobInfo);
        log.info("Successfully created JobInfo(id={})", jobInfo.getId());
        return ResponseEntity.ok(ApiResponse.ok("创建成功", response));
    }

    /**
     * Delete JobInfo.
     * 
     * @apiNote DELETE /api/jobs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJobInfo(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Deleting JobInfo(id={})", id);
        jobInfoService.delete(id);
        log.info("Successfully deleted JobInfo(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

    /**
     * Update JobInfo.
     * 
     * @apiNote PUT /api/jobs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobInfoResponse>> updateJobInfo(
        @PathVariable @NotNull @Positive Integer id,
        @Valid @RequestBody JobInfoUpdateRequest request
    ) {
        log.info("Updating JobInfo(id={})", id);
        JobInfo jobInfo = jobInfoService.update(id, request);
        JobInfoResponse response = JobInfoResponse.from(jobInfo);
        log.info("Successfully updated JobInfo(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
    }

    /**
     * Retrieves a JobInfo.
     * 
     * @apiNote GET /api/jobs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobInfoResponse>> getJobInfo(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Retrieving JobInfo(id={})", id);
        JobInfo jobInfo = jobInfoService.getById(id);
        JobInfoResponse response = JobInfoResponse.from(jobInfo);
        log.info("Successfully retrieved JobInfo(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Retrieves JobInfos with pagination.
     * 
     * @apiNote GET /api/jobs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobInfoResponse>>> getAllJobInfos(
        @Valid PageRequest request
    ) {
        log.info("Retrieving JobInfos with {}", request);

        // Fetch results.
        Page<JobInfo> results = jobInfoService.getAll(request);

        // Fetch results -> response DTOs -> final response.
        Page<JobInfoResponse> dtos = results.map(JobInfoResponse::from);
        PageResponse<JobInfoResponse> response = PageResponse.from(dtos);

        log.info("Successfully retrieved JobInfos");
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Search JobInfos with query conditions.
     * 
     * @apiNote GET /api/jobs/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<JobInfoResponse>>> searchJobInfos(
        @Valid JobInfoQueryRequest request
    ) {
        log.info("Searching JobInfos with {}", request);

        // Fetch query results.
        Page<JobInfo> results = jobInfoService.query(request);

        // Fetch results -> response DTOs -> final response.
        Page<JobInfoResponse> dtos = results.map(JobInfoResponse::from);
        PageResponse<JobInfoResponse> response = PageResponse.from(dtos);

        log.info("Successfully searched JobInfos");
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }
}
