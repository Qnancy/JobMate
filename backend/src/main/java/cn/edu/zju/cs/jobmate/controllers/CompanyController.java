package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.services.CompanyService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Company REST Controller.
 */
@Slf4j
@RestController
@RequestMapping("/api/companies")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Create a new company.
     * 
     * @apiNote POST /api/companies
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
        @Valid @RequestBody CompanyCreateRequest request
    ) {
        log.info("Creating company with name: {}, type: {}",
            request.getName(), request.getType());
        Company company = new Company(request.getName(), request.getType());
        company = companyService.create(company);
        CompanyResponse response = CompanyResponse.from(company);
        log.info("Successfully created company, assigned ID: {}", company.getId());
        return ResponseEntity.ok(ApiResponse.ok("创建成功", response));
    }

    /**
     * Delete company.
     * 
     * @apiNote DELETE /api/companies/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Deleting company with ID: {}", id);
        companyService.delete(id);
        log.info("Successfully deleted company with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

    /**
     * Update company.
     * 
     * @apiNote PUT /api/companies/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
        @PathVariable @NotNull @Positive Integer id,
        @Valid @RequestBody CompanyUpdateRequest request
    ) {
        log.info("Updating company with ID: {}", id);
        Company company = companyService.update(id, request.getName(), request.getType());
        CompanyResponse response = CompanyResponse.from(company);
        log.info("Successfully updated company with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
    }

    /**
     * Retrieves a company by ID.
     * 
     * @apiNote GET /api/companies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Retrieving company with ID: {}", id);
        Company company = companyService.getById(id);
        CompanyResponse response = CompanyResponse.from(company);
        log.info("Successfully retrieved id: {}", company.getId());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Retrieves companies with pagination.
     * 
     * @apiNote GET /api/companies
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> getAllCompanies(
        @Valid CompanyQueryRequest request
    ) {
        int page = request.getPage();
        int pageSize = request.getPageSize();
        CompanyType type = request.getType();
        log.info("Retrieving companies - page: {}, page_size: {}, type: {}",
            page, pageSize, type);

        // Fetch query results.
        Page<Company> results;
        if (type != null) {
            results = companyService.getByType(type, page - 1, pageSize);
        } else {
            results = companyService.getAll(page - 1, pageSize);
        }

        // Query results -> response DTOs -> final response.
        Page<CompanyResponse> dtos = results.map(CompanyResponse::from);
        PageResponse<CompanyResponse> response = PageResponse.from(dtos);

        log.info("Successfully retrieved {} companies", response.getPageSize());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }
}
