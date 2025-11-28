package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.dto.company.CompanyCreateRequest;
import cn.edu.zju.cs.jobmate.dto.company.CompanyResponse;
import cn.edu.zju.cs.jobmate.dto.company.CompanyUpdateRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Company REST Controller
 * 
 * Provides RESTful APIs for company management operations
 * 
 * @author JobMate Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/companies")
@CrossOrigin(originPatterns = "*")
@Validated
public class CompanyController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    private final CompanyService companyService;

    /**
     * Constructor injection for CompanyService
     */
    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Creates a new company
     * POST /api/companies
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @Valid @RequestBody CompanyCreateRequest request) {
        
        logger.info("Creating company with name: {}, type: {}", request.getName(), request.getType());
        
        Company company = new Company(request.getName(), request.getType());
        Company savedCompany = companyService.create(company);
        CompanyResponse response = CompanyResponse.from(savedCompany);
        
        logger.info("Successfully created company with ID: {}", savedCompany.getId());
        return ResponseEntity.ok(ApiResponse.ok("公司创建成功", response));
    }

    /**
     * Retrieves companies with pagination
     * GET /api/companies
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> getAllCompanies(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(required = false) CompanyType type) {
        
        logger.info("Retrieving companies - page: {}, page_size: {}, type: {}", page, pageSize, type);
        
        try {
            // Convert to 0-based page for Spring Data
            int springPage = page - 1;
            
            Page<Company> companyPage;
            if (type != null) {
                companyPage = companyService.getByType(type, springPage, pageSize);
            } else {
                companyPage = companyService.getAll(springPage, pageSize);
            }
            
            List<CompanyResponse> responses = companyPage.getContent().stream()
                    .map(CompanyResponse::from)
                    .collect(Collectors.toList());
            
            PageResponse<CompanyResponse> pageResponse = new PageResponse<>(
                responses, 
                companyPage.getTotalElements(),
                page,  // Return 1-based page to frontend
                pageSize
            );
            
            logger.info("Successfully retrieved {} companies", responses.size());
            return ResponseEntity.ok(ApiResponse.ok("查询成功", pageResponse));
            
        } catch (Exception e) {
            logger.error("Error retrieving companies: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a company by its ID
     * GET /api/companies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
            @PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Retrieving company with ID: {}", id);
        
        Company company = companyService.getCompanyById(id);
        CompanyResponse response = CompanyResponse.from(company);
        logger.info("Successfully retrieved company: {}", company.getName());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Update company
     * PUT /api/companies/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @PathVariable @NotNull @Positive Integer id,
            @Valid @RequestBody CompanyUpdateRequest request) {
        
        logger.info("Updating company with ID: {}", id);
        
        Company savedCompany = companyService.updateById(id, request.getName(), request.getType());
        CompanyResponse response = CompanyResponse.from(savedCompany);
        
        logger.info("Successfully updated company with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
    }

    /**
     * Delete company
     * DELETE /api/companies/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable @NotNull @Positive Integer id) {
        
        logger.info("Deleting company with ID: {}", id);
        
        companyService.deleteCompanyById(id);
        logger.info("Successfully deleted company with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

}