package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Company service implementation.
 * 
 * @see CompanyService
 */
@Slf4j
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Company create(CompanyCreateRequest dto) {
        if (companyRepository.existsByName(dto.getName())) {
            throw new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
        }
        return companyRepository.save(Objects.requireNonNull(dto.toModel()));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        companyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Company update(Integer id, CompanyUpdateRequest dto) {
        // Check if no update needed.
        if (!dto.isUpdatable()) {
            throw new BusinessException(ErrorCode.NO_UPDATES);
        }

        // Fetch existing entity.
        Company company = getById(id);

        // Update entity properties.
        dto.apply(company);

        // Update.
        return companyRepository.save(Objects.requireNonNull(company));
    }

    @Override
    @Transactional(readOnly = true)
    public Company getById(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        return companyRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Company getByName(String name) {
        return companyRepository.findByName(name)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> getByType(CompanyType type) {
        return companyRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Company> getAll(CompanyQueryRequest dto) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
        Page<Company> results = dto.getType() == null ?
            companyRepository.findAll(pageable) :
            companyRepository.findByType(dto.getType(), pageable);
        log.info("Totally retrieved {} companies", results.getTotalElements());
        return results;
    }
}
