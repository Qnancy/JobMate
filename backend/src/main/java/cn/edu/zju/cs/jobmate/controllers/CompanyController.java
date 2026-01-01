package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.services.CompanyService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Create a new Company.
     * 
     * @apiNote POST /api/companies
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyResponse>> create(
        @Valid @RequestBody CompanyCreateRequest request
    ) {
        log.info("Creating Company with {}", request);
        Company company = companyService.create(request);
        CompanyResponse response = CompanyResponse.from(company);
        log.info("Successfully created Company(id={})", company.getId());
        return ResponseEntity.ok(ApiResponse.ok("创建成功", response));
    }

    /**
     * Delete Company.
     * 
     * @apiNote DELETE /api/companies/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Deleting Company(id={})", id);
        companyService.delete(id);
        log.info("Successfully deleted Company(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功"));
    }

    /**
     * Update Company.
     * 
     * @apiNote PUT /api/companies/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CompanyResponse>> update(
        @PathVariable @NotNull @Positive Integer id,
        @Valid @RequestBody CompanyUpdateRequest request
    ) {
        log.info("Updating Company(id={}) with {}", id, request);
        Company company = companyService.update(id, request);
        CompanyResponse response = CompanyResponse.from(company);
        log.info("Successfully updated Company(id={})", id);
        return ResponseEntity.ok(ApiResponse.ok("更新成功", response));
    }

    /**
     * Retrieves a Company by ID.
     * 
     * @apiNote GET /api/companies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> get(
        @PathVariable @NotNull @Positive Integer id
    ) {
        log.info("Retrieving Company(id={})", id);
        Company company = companyService.getById(id);
        CompanyResponse response = CompanyResponse.from(company);
        log.info("Successfully retrieved Company(id={})", company.getId());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Retrieves Companies with pagination.
     * 
     * @apiNote GET /api/companies
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> getAll(
        @Valid PageRequest request
    ) {
        log.info("Retrieving Companies with {}", request);

        // Fetch query results.
        Page<Company> results = companyService.getAll(request);

        // Query results -> response DTOs -> final response.
        Page<CompanyResponse> dtos = results.map(CompanyResponse::from);
        PageResponse<CompanyResponse> response = PageResponse.from(dtos);

        log.info("Successfully retrieved {} Companies", response.getCount());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }

    /**
     * Search Companies with query conditions.
     * 
     * @apiNote GET /api/companies/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> search(
        @Valid CompanyQueryRequest request
    ) {
        log.info("Searching Companies with {}", request);

        // Fetch query results.
        Page<Company> results = companyService.query(request);

        // Query results -> response DTOs -> final response.
        Page<CompanyResponse> dtos = results.map(CompanyResponse::from);
        PageResponse<CompanyResponse> response = PageResponse.from(dtos);

        log.info("Successfully searched {} Companies", response.getCount());
        return ResponseEntity.ok(ApiResponse.ok("查询成功", response));
    }
}
