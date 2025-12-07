package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.services.impl.CompanyServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    private CompanyRepository companyRepository;
    private CompanyServiceImpl companyService;

    @BeforeEach
    void setUp() {
        companyRepository = mock(CompanyRepository.class);
        companyService = new CompanyServiceImpl(companyRepository);
    }

    @Test
    void testCreateCompany_Success() {
        Company company = new Company("Test", CompanyType.STATE);
        when(companyRepository.existsByName("Test")).thenReturn(false);
        when(companyRepository.save(company)).thenReturn(company);

        Company result = companyService.create(company);
        assertEquals("Test", result.getName());
        assertEquals(CompanyType.STATE, result.getType());
        verify(companyRepository).save(company);
    }

    @Test
    void testCreateCompany_AlreadyExists() {
        Company company = new Company("Test", CompanyType.STATE);
        when(companyRepository.existsByName("Test")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> companyService.create(company));
        assertEquals(ErrorCode.COMPANY_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void testGetById_Success() {
        Company company = new Company("Test", CompanyType.STATE);
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        Company result = companyService.getById(1);
        assertEquals("Test", result.getName());
    }

    @Test
    void testGetById_NotFound() {
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
            () -> companyService.getById(1));
        assertEquals(ErrorCode.COMPANY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testUpdateCompany_Success() {
        Company company = new Company("Old", CompanyType.STATE);
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Company updated = companyService.update(1, "New", CompanyType.PRIVATE);
        assertEquals("New", updated.getName());
        assertEquals(CompanyType.PRIVATE, updated.getType());
    }

    @Test
    void testDeleteCompany_Success() {
        doNothing().when(companyRepository).deleteById(1);
        companyService.delete(1);
        verify(companyRepository).deleteById(1);
    }

    @Test
    void testDeleteCompany_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> companyService.delete(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    void testGetByType() {
        List<Company> companies = List.of(
            new Company("A", CompanyType.STATE),
            new Company("B", CompanyType.STATE)
        );
        when(companyRepository.findByType(CompanyType.STATE)).thenReturn(companies);

        List<Company> result = companyService.getByType(CompanyType.STATE);
        assertEquals(2, result.size());
    }

    @Test
    void testGetByTypeWithPagination() {
        List<Company> companies = List.of(
            new Company("A", CompanyType.STATE),
            new Company("B", CompanyType.STATE)
        );
        Page<Company> page = new PageImpl<>(
            companies,
            PageRequest.of(0, 2), 2
        );
        when(companyRepository.findByType(
            eq(CompanyType.STATE),
            any(PageRequest.class))
        ).thenReturn(page);

        Page<Company> result = companyService.getByType(CompanyType.STATE, 0, 2);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testGetAll() {
        List<Company> companies = List.of(
            new Company("A", CompanyType.STATE),
            new Company("B", CompanyType.PRIVATE)
        );
        when(companyRepository.findAll()).thenReturn(companies);

        List<Company> result = companyService.getAll();
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllWithPagination() {
        List<Company> companies = List.of(
            new Company("A", CompanyType.STATE),
            new Company("B", CompanyType.PRIVATE)
        );
        Page<Company> page = new PageImpl<>(
            companies,
            PageRequest.of(0, 2), 2
        );
        when(companyRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Company> result = companyService.getAll(0, 2);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }
}
