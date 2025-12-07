package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
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
    public Company create(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
        }
        return companyRepository.save(company);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        // TODO: add checking for existing if necessary.
        companyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Company update(Integer id, String name, CompanyType type) {
        // Fetch existing entity.
        Company company = getById(id);

        // Update entity properties.
        company.setName(name);
        company.setType(type);

        // Update.
        return companyRepository.save(company);
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
    public Page<Company> getByType(CompanyType type, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return companyRepository.findByType(type, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Company> getAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return companyRepository.findAll(pageable);
    }
}
