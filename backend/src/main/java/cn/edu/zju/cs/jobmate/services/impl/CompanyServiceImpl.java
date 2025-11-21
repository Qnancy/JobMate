package cn.edu.zju.cs.jobmate.services.impl;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for Company entity.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company create(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new IllegalArgumentException("Company with name " + company.getName() + " already exists");
        }
        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return companyRepository.findById(id);
    }

    @Override
    public Optional<Company> getByName(String name) {
        return companyRepository.findByName(name);
    }

    @Override
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    @Override
    public Page<Company> getAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return companyRepository.findAll(pageable);
    }

    @Override
    public List<Company> getByType(CompanyType type) {
        return companyRepository.findByType(type);
    }

    @Override
    public Page<Company> getByType(CompanyType type, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return companyRepository.findByType(type, pageable);
    }

    @Override
    public Company update(Company company) {
        Integer id = company.getId();
        if (id == null || !companyRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return companyRepository.save(company);
    }

    @Override
    public Company updateById(Integer id, String name, CompanyType type) {
        if (id == null || !companyRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // Use custom update query to modify specific fields
        companyRepository.updateCompanyById(id, name, type);
        
        // Return the updated company
        return companyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Failed to retrieve updated company with id " + id));
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            companyRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return id != null && companyRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return companyRepository.existsByName(name);
    }
}

