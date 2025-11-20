package cn.edu.zju.cs.jobmate.services.impl;

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
    @Transactional(readOnly = true)
    public Optional<Company> getById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return companyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> getByName(String name) {
        return companyRepository.findByName(name);
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
    public Company update(Company company) {
        Integer id = company.getId();
        if (id == null || !companyRepository.existsById(id)) {
            throw new IllegalArgumentException("Company with id " + id + " does not exist");
        }
        return companyRepository.save(company);
    }

    @Override
    public void deleteById(Integer id) {
        if (id != null) {
            companyRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return id != null && companyRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return companyRepository.existsByName(name);
    }
}

