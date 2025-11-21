package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoCreateRequest;
import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoResponse;
import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoUpdateRequest;
import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
import cn.edu.zju.cs.jobmate.services.CompanyService;
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
 * ActivityInfo REST Controller
 * 
 * Provides RESTful APIs for activity information management operations
 * 
 * @author JobMate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/activities")
@CrossOrigin(originPatterns = "*")
@Validated
public class ActivityInfoController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityInfoController.class);

    private final ActivityInfoService activityInfoService;
    private final CompanyService companyService;

    /**
     * Constructor injection for services
     */
    @Autowired
    public ActivityInfoController(ActivityInfoService activityInfoService, CompanyService companyService) {
        this.activityInfoService = activityInfoService;
        this.companyService = companyService;
    }

    /**
     * Creates a new activity info
     * POST /api/activities
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ActivityInfoResponse>> createActivityInfo(
            @Valid @RequestBody ActivityInfoCreateRequest request) {
        
        logger.info("Creating activity info for company: {}, title: {}", 
                   request.getCompany().getName(), request.getTitle());
        
        try {
            // Validate company exists
            Optional<Company> company = companyService.getById(request.getCompany().getId());
            if (!company.isPresent()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
            // Create ActivityInfo
            ActivityInfo activityInfo = new ActivityInfo(
                company.get(),
                request.getTitle(),
                request.getTime()
            );
            activityInfo.setLink(request.getLink());
            activityInfo.setLocation(request.getLocation());
            activityInfo.setExtra(request.getExtra());
            
            ActivityInfo savedActivityInfo = activityInfoService.create(activityInfo);
            ActivityInfoResponse response = ActivityInfoResponse.from(savedActivityInfo);
            
            logger.info("Successfully created activity info with ID: {}", savedActivityInfo.getId());
            return ResponseEntity.ok(ApiResponse.ok("宣讲会信息创建成功", response));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating activity info: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves an activity info by its ID
     * GET /api/activities/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityInfoResponse>> getActivityInfo(
            @PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Retrieving activity info with ID: {}", id);
        
        try {
            Optional<ActivityInfo> activityInfo = activityInfoService.getById(id);
            
            if (activityInfo.isPresent()) {
                ActivityInfoResponse response = ActivityInfoResponse.from(activityInfo.get());
                logger.info("Successfully retrieved activity info: {}", activityInfo.get().getTitle());
                return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
            } else {
                logger.warn("Activity info not found with ID: {}", id);
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving activity info with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update activity info
     * PUT /api/activities/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityInfoResponse>> updateActivityInfo(
            @PathVariable @NotNull @Positive Integer id,
            @Valid @RequestBody ActivityInfoUpdateRequest request) {
        
        logger.info("Updating activity info with ID: {}", id);
        
        try {
            // Validate company exists
            Optional<Company> company = companyService.getById(request.getCompany().getId());
            if (!company.isPresent()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
            // Use the new updateById method that handles ID preservation
            ActivityInfo savedActivityInfo = activityInfoService.updateById(
                id, 
                request.getCompany().getId(), 
                request.getTitle(), 
                request.getTime(), 
                request.getLink(), 
                request.getLocation(), 
                request.getExtra()
            );
            ActivityInfoResponse response = ActivityInfoResponse.from(savedActivityInfo);
            
            logger.info("Successfully updated activity info with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating activity info with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete activity info
     * DELETE /api/activities/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivityInfo(@PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Deleting activity info with ID: {}", id);
        
        try {
            if (!activityInfoService.existsById(id)) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            }
            
            activityInfoService.deleteById(id);
            logger.info("Successfully deleted activity info with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.ok("删除成功"));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting activity info with ID {}: {}", id, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves activity infos with pagination
     * GET /api/activities
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ActivityInfoResponse>>> getAllActivityInfos(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(100) Integer pageSize) {
        
        logger.info("Retrieving activity infos - page: {}, page_size: {}", page, pageSize);
        
        try {
            // Convert to 0-based page for Spring Data
            int springPage = page - 1;
            
            Page<ActivityInfo> activityInfoPage = activityInfoService.getAll(springPage, pageSize);
            
            List<ActivityInfoResponse> responses = activityInfoPage.getContent().stream()
                    .map(ActivityInfoResponse::from)
                    .collect(Collectors.toList());
            
            PageResponse<ActivityInfoResponse> pageResponse = new PageResponse<>(
                responses, 
                activityInfoPage.getTotalElements(),
                page,  // Return 1-based page to frontend
                pageSize
            );
            
            logger.info("Successfully retrieved {} activity infos", responses.size());
            return ResponseEntity.ok(ApiResponse.ok("查询成功", pageResponse));
            
        } catch (Exception e) {
            logger.error("Error retrieving activity infos: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search activity infos with conditions
     * GET /api/activities/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ActivityInfoResponse>>> searchActivityInfos(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        logger.info("Searching activity infos - page: {}, page_size: {}, keyword: {}", 
                   page, pageSize, keyword);
        
        try {
            // Convert to 0-based page for Spring Data
            int springPage = page - 1;
            
            // Service's query method handles all condition combinations automatically:
            // - keyword=null → all records with pagination
            // - keyword=value → keyword search in title and company_name
            Page<ActivityInfo> activityInfoPage = activityInfoService.query(keyword, springPage, pageSize);
            
            List<ActivityInfoResponse> responses = activityInfoPage.getContent().stream()
                    .map(ActivityInfoResponse::from)
                    .collect(Collectors.toList());
            
            PageResponse<ActivityInfoResponse> pageResponse = new PageResponse<>(
                responses, 
                activityInfoPage.getTotalElements(),
                page,  // Return 1-based page to frontend
                pageSize
            );
            
            logger.info("Successfully retrieved {} activity infos", responses.size());
            return ResponseEntity.ok(ApiResponse.ok("查询成功", pageResponse));
            
        } catch (Exception e) {
            logger.error("Error searching activity infos: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}