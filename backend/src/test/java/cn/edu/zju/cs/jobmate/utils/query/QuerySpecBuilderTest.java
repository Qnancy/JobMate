package cn.edu.zju.cs.jobmate.utils.query;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class QuerySpecBuilderTest {

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company1;
    private Company company2;
    private Company company3;

    private JobInfo job1;
    private JobInfo job2;
    private JobInfo job3;
    private JobInfo job4;

    private static QuerySpecBuilder.Fields fields = QuerySpecBuilder.Fields.of(
        "company.name", "position", "city", "location");
    private QuerySpecBuilder.Filter filter;

    @BeforeEach
    @SuppressWarnings("null")
    void setUp() {
        company1 = companyRepository.save(new Company(
            "Very six company",
            CompanyType.PRIVATE
        ));
        company2 = companyRepository.save(new Company(
                "Somebody else",
                CompanyType.PRIVATE
        ));
        company3 = companyRepository.save(new Company(
                "Another one",
                CompanyType.STATE
        ));

        job1 = new JobInfo(
            RecruitType.INTERN,
            "Software Engineer Intern",
            null,
            "Hangzhou",
            null,
            null
        );
        job1.setCompany(company1);
        job1 = jobInfoRepository.save(job1);
        job2 = new JobInfo(
            RecruitType.EXPERIENCED,
            "Backend Developer",
            null,
            "Beijing",
            null,
            null
        );
        job2.setCompany(company2);
        job2 = jobInfoRepository.save(job2);
        job3 = new JobInfo(
            RecruitType.CAMPUS,
            "Frontend Developer",
            null,
            "Hangzhou",
            null,
            null
        );
        job3.setCompany(company3);
        job3 = jobInfoRepository.save(job3);
        job4 = new JobInfo(
            RecruitType.CAMPUS,
            "Fullstack Developer",
            null,
            "Hangzhou",
            null,
            null
        );
        job4.setCompany(company1);
        job4 = jobInfoRepository.save(job4);
    }

    @Test
    void testKeywordQuery() {
        filter = QuerySpecBuilder.Filter.of("recruitType", null);
        Specification<JobInfo> spec = QuerySpecBuilder.build(
            "Hangzhou dev",
            fields,
            filter
        );
        List<JobInfo> result = jobInfoRepository.findAll(spec);
        assertEquals(2, result.size());
        assertTrue(result.contains(job3));
        assertTrue(result.contains(job4));
    }

    @Test
    void testFilterQuery() {
        filter = QuerySpecBuilder.Filter.of("recruitType", RecruitType.CAMPUS);
        Specification<JobInfo> spec = QuerySpecBuilder.build(
            null,
            fields,
            filter
        );
        List<JobInfo> result = jobInfoRepository.findAll(spec);
        assertEquals(2, result.size());
        assertTrue(result.contains(job3));
        assertTrue(result.contains(job4));
    }

    @Test
    void testKeywordAndFilterQuery() {
        filter = QuerySpecBuilder.Filter.of("recruitType", RecruitType.CAMPUS);
        Specification<JobInfo> spec = QuerySpecBuilder.build(
            "Hangzhou dev",
            fields,
            filter
        );
        List<JobInfo> result = jobInfoRepository.findAll(spec);
        assertEquals(2, result.size());
        assertTrue(result.contains(job3));
        assertTrue(result.contains(job4));
    }
}
