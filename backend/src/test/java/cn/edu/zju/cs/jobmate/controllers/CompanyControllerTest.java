package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import cn.edu.zju.cs.jobmate.dto.common.PageResponse;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyControllerTest {

    private CompanyService companyService;
    private CompanyController companyController;

    @BeforeEach
    void setUp() {
        companyService = mock(CompanyService.class);
        companyController = new CompanyController(companyService);
    }

    @Test
    void testCreateCompany() {
        CompanyCreateRequest request = CompanyCreateRequest.builder()
            .name("Test")
            .type(CompanyType.STATE)
            .build();
        Company company = new Company("Test", CompanyType.STATE);

        when(companyService.create(any(Company.class))).thenReturn(company);

        ResponseEntity<ApiResponse<CompanyResponse>> response = companyController.createCompany(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<CompanyResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals("Test", body.getData().getName());
        assertEquals(CompanyType.STATE, body.getData().getType());
    }

    @Test
    void testDeleteCompany() {
        doNothing().when(companyService).delete(1);

        ResponseEntity<ApiResponse<Void>> response = companyController.deleteCompany(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertNull(body.getData());
    }

    @Test
    void testUpdateCompany() {
        CompanyUpdateRequest request = CompanyUpdateRequest.builder()
                .name("Updated")
                .type(CompanyType.PRIVATE)
                .build();
        Company company = new Company("Updated", CompanyType.PRIVATE);
        try {
            Field idField = Company.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(company, 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set company ID via reflection");
        }

        when(companyService.update(eq(1), eq("Updated"), eq(CompanyType.PRIVATE)))
            .thenReturn(company);

        ResponseEntity<ApiResponse<CompanyResponse>> response = companyController.updateCompany(1, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<CompanyResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals("Updated", body.getData().getName());
        assertEquals(CompanyType.PRIVATE, body.getData().getType());
    }

    @Test
    void testGetCompany() {
        Company company = new Company("Test", CompanyType.STATE);

        when(companyService.getById(1)).thenReturn(company);

        ResponseEntity<ApiResponse<CompanyResponse>> response = companyController.getCompany(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<CompanyResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals("Test", body.getData().getName());
    }

    @Test
    void testGetAllCompanies() {
        CompanyQueryRequest request = CompanyQueryRequest.builder()
            .page(1)
            .pageSize(10)
            .type(null)
            .build();

        List<Company> companies = List.of(
                new Company("A", CompanyType.STATE),
                new Company("B", CompanyType.PRIVATE)
        );

        Page<Company> page = new PageImpl<>(Objects.requireNonNull(companies));

        when(companyService.getAll(0, 10)).thenReturn(page);

        ResponseEntity<ApiResponse<PageResponse<CompanyResponse>>> response = companyController.getAllCompanies(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<PageResponse<CompanyResponse>> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getData().getContent().size());
        assertEquals("A", body.getData().getContent().get(0).getName());
        assertEquals("B", body.getData().getContent().get(1).getName());
    }
}
