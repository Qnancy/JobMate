package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.dto.job.JobInfoCreateRequest;
import cn.edu.zju.cs.jobmate.dto.job.JobInfoResponse;
import cn.edu.zju.cs.jobmate.dto.job.JobInfoUpdateRequest;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JobInfo REST Controller
 * 
 * Provides RESTful APIs for job information management operations
 * 
 * @author JobMate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(originPatterns = "*")
@Validated
public class JobInfoController {

    private static final Logger logger = LoggerFactory.getLogger(JobInfoController.class);

    private final JobInfoService jobInfoService;
    private final CompanyService companyService;

    /**
     * Constructor injection for services
     */
    @Autowired
    public JobInfoController(JobInfoService jobInfoService, CompanyService companyService) {
        this.jobInfoService = jobInfoService;
        this.companyService = companyService;
    }

    /**
     * Creates a new job info
     * POST /api/jobs
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JobInfoResponse>> createJobInfo(
            @Valid @RequestBody JobInfoCreateRequest request) {
        
        logger.info("Creating job info for company: {}, position: {}", 
                   request.getCompany().getName(), request.getPosition());
        
        try {
            // Validate company exists
            Optional<Company> company = companyService.getById(request.getCompany().getId());
            if (!company.isPresent()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
            // Create JobInfo
            JobInfo jobInfo = new JobInfo(
                company.get(),
                request.getRecruitType(),
                request.getPosition(),
                request.getLink()
            );
            jobInfo.setLocation(request.getLocation());
            jobInfo.setExtra(request.getExtra());
            
            JobInfo savedJobInfo = jobInfoService.create(jobInfo);
            JobInfoResponse response = JobInfoResponse.from(savedJobInfo);
            
            logger.info("Successfully created job info with ID: {}", savedJobInfo.getId());
            return ResponseEntity.ok(ApiResponse.ok("招聘信息创建成功", response));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating job info: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a job info by its ID
     * GET /api/jobs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobInfoResponse>> getJobInfo(
            @PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Retrieving job info with ID: {}", id);
        
        try {
            Optional<JobInfo> jobInfo = jobInfoService.getById(id);
            
            if (jobInfo.isPresent()) {
                JobInfoResponse response = JobInfoResponse.from(jobInfo.get());
                logger.info("Successfully retrieved job info: {}", jobInfo.get().getPosition());
                return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
            } else {
                logger.warn("Job info not found with ID: {}", id);
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving job info with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update job info
     * PUT /api/jobs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobInfoResponse>> updateJobInfo(
            @PathVariable @NotNull @Positive Integer id,
            @Valid @RequestBody JobInfoUpdateRequest request) {
        
        logger.info("Updating job info with ID: {}", id);
        
        try {
            // Validate company exists
            Optional<Company> company = companyService.getById(request.getCompany().getId());
            if (!company.isPresent()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
            // Use the new updateById method that handles ID preservation
            JobInfo savedJobInfo = jobInfoService.updateById(
                id, 
                request.getCompany().getId(), 
                request.getRecruitType(), 
                request.getPosition(), 
                request.getLink(), 
                request.getLocation(), 
                request.getExtra()
            );
            JobInfoResponse response = JobInfoResponse.from(savedJobInfo);
            
            logger.info("Successfully updated job info with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating job info with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete job info
     * DELETE /api/jobs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJobInfo(@PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Deleting job info with ID: {}", id);
        
        try {
            if (!jobInfoService.existsById(id)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
            jobInfoService.deleteById(id);
            logger.info("Successfully deleted job info with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("删除成功"));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting job info with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves job infos with pagination
     * GET /api/jobs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobInfoResponse>>> getAllJobInfos(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        
        logger.info("Retrieving job infos - page: {}, page_size: {}", page, pageSize);
        
        try {
            // Convert to 0-based page for Spring Data
            int springPage = page - 1;
            
            Page<JobInfo> jobInfoPage = jobInfoService.getAll(springPage, pageSize);
            
            List<JobInfoResponse> responses = jobInfoPage.getContent().stream()
                    .map(JobInfoResponse::from)
                    .collect(Collectors.toList());
            
            PageResponse<JobInfoResponse> pageResponse = new PageResponse<>(
                responses, 
                jobInfoPage.getTotalElements(),
                page,  // Return 1-based page to frontend
                pageSize
            );
            
            logger.info("Successfully retrieved {} job infos", responses.size());
            return ResponseEntity.ok(ApiResponse.ok("查询成功", pageResponse));
            
        } catch (Exception e) {
            logger.error("Error retrieving job infos: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search job infos with conditions
     * GET /api/jobs/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<JobInfoResponse>>> searchJobInfos(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(name = "recruit_type", required = false) RecruitType recruitType) {
        
        logger.info("Searching job infos - page: {}, page_size: {}, keyword: {}, recruit_type: {}", 
                   page, pageSize, keyword, recruitType);
        
        try {
            // Convert to 0-based page for Spring Data
            int springPage = page - 1;
            
            // Service's query method handles all condition combinations automatically:
            // - keyword=null & recruitType=null → all records with pagination
            // - keyword=null & recruitType=value → filter by recruitType only  
            // - keyword=value & recruitType=null/value → keyword search with optional type filter
            Page<JobInfo> jobInfoPage = jobInfoService.query(keyword, recruitType, springPage, pageSize);
            
            List<JobInfoResponse> responses = jobInfoPage.getContent().stream()
                    .map(JobInfoResponse::from)
                    .collect(Collectors.toList());
            
            PageResponse<JobInfoResponse> pageResponse = new PageResponse<>(
                responses, 
                jobInfoPage.getTotalElements(),
                page,  // Return 1-based page to frontend
                pageSize
            );
            
            logger.info("Successfully retrieved {} job infos", responses.size());
            return ResponseEntity.ok(ApiResponse.ok("查询成功", pageResponse));
            
        } catch (Exception e) {
            logger.error("Error searching job infos: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}