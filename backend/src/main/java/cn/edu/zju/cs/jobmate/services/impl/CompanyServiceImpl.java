package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import cn.edu.zju.cs.jobmate.utils.query.QuerySpecBuilder;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<Company> getAll(PageRequest dto) {
        return companyRepository.findAll(dto.toPageable());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Company> query(CompanyQueryRequest dto) {
        Specification<Company> spec = QuerySpecBuilder.build(
            dto.getKeyword(),
            QuerySpecBuilder.Fields.of("name"),
            QuerySpecBuilder.Filter.of("type", dto.getType())
        );
        return companyRepository.findAll(spec, dto.toPageable());
    }
}
