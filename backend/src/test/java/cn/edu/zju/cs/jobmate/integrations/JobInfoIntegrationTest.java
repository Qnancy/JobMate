package cn.edu.zju.cs.jobmate.integrations;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.zju.cs.jobmate.dto.job.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JobInfoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Company company1;
    private Company company2;

    @BeforeEach
    void setUp() {
        company1 = companyRepository.save(new Company(
            "Company 1",
            CompanyType.FOREIGN
        ));
        company2 = companyRepository.save(new Company(
            "Company 2",
            CompanyType.STATE
        ));

        JobInfo jobInfo1 = new JobInfo(
            RecruitType.CAMPUS,
            "Position 1",
            "https://example.com/1",
            "City 1",
            null
        );
        jobInfo1.setCompany(company1);
        jobInfoRepository.save(jobInfo1);
        JobInfo jobInfo2 = new JobInfo(
            RecruitType.INTERN,
            "Position 2",
            "https://example.com/2",
            "City 2",
            null
        );
        jobInfo2.setCompany(company1);
        jobInfoRepository.save(jobInfo2);
        JobInfo jobInfo3 = new JobInfo(
            RecruitType.CAMPUS,
            "Position 3",
            "https://example.com/3",
            "City 3",
            null
        );
        jobInfo3.setCompany(company2);
        jobInfoRepository.save(jobInfo3);
    }

    @AfterEach
    void clean() {
        jobInfoRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("null")
    void testCreateJobInfo_Success() throws Exception {
        JobInfoCreateRequest request = JobInfoCreateRequest.builder()
            .companyId(company1.getId())
            .recruitType(RecruitType.CAMPUS)
            .position("Test Position")
            .link(null)
            .location("Hangzhou")
            .extra(null)
            .build();

        mockMvc.perform(post("/api/jobs")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.company.id").value(company1.getId()))
            .andExpect(jsonPath("$.data.company.name").value(company1.getName()))
            .andExpect(jsonPath("$.data.company.type").value(company1.getType().name()))
            .andExpect(jsonPath("$.data.position").value("Test Position"))
            .andExpect(jsonPath("$.data.link").isEmpty())
            .andExpect(jsonPath("$.data.location").value("Hangzhou"))
            .andExpect(jsonPath("$.data.extra").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testCreateJobInfo_CompanyNotFound() throws Exception {
        JobInfoCreateRequest request = JobInfoCreateRequest.builder()
            .companyId(9999)
            .recruitType(RecruitType.CAMPUS)
            .position("Test Position")
            .link(null)
            .location(null)
            .extra(null)
            .build();

        mockMvc.perform(post("/api/jobs")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("企业不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteJobInfo() throws Exception {
        JobInfo jobInfo = jobInfoRepository.findAll().get(0);

        mockMvc.perform(delete("/api/jobs/{id}", jobInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateJobInfo_Success() throws Exception {
        Company company = companyRepository.save(new Company(
            "Company 3",
            CompanyType.PRIVATE
        ));
        JobInfo jobInfo = jobInfoRepository.findAll().get(0);

        JobInfoUpdateRequest request = JobInfoUpdateRequest.builder()
            .companyId(company.getId())
            .recruitType(null)
            .position("Updated Position")
            .link("https://updated-link.com")
            .location(null)
            .extra(null)
            .build();

        mockMvc.perform(put("/api/jobs/{id}", jobInfo.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.id").value(jobInfo.getId()))
            .andExpect(jsonPath("$.data.company.id").value(company.getId()))
            .andExpect(jsonPath("$.data.company.name").value(company.getName()))
            .andExpect(jsonPath("$.data.company.type").value(company.getType().name()))
            .andExpect(jsonPath("$.data.recruit_type").value(jobInfo.getRecruitType().name()))
            .andExpect(jsonPath("$.data.position").value("Updated Position"))
            .andExpect(jsonPath("$.data.link").value("https://updated-link.com"))
            .andExpect(jsonPath("$.data.location").value(jobInfo.getLocation()))
            .andExpect(jsonPath("$.data.extra").value(jobInfo.getExtra()));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateJobInfo_NoUpdate() throws Exception {
        JobInfo jobInfo = jobInfoRepository.findAll().get(0);

        JobInfoUpdateRequest request = JobInfoUpdateRequest.builder()
            .recruitType(null)
            .position(null)
            .link(null)
            .location(null)
            .extra(null)
            .build();

        mockMvc.perform(put("/api/jobs/{id}", jobInfo.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("没有更新"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateJobInfo_CompanyNotFound() throws Exception {
        JobInfo jobInfo = jobInfoRepository.findAll().get(0);

        JobInfoUpdateRequest request = JobInfoUpdateRequest.builder()
            .companyId(9999)
            .recruitType(null)
            .position("Updated Position")
            .link("https://updated-link.com")
            .location(null)
            .extra(null)
            .build();

        mockMvc.perform(put("/api/jobs/{id}", jobInfo.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("企业不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateJobInfo_JobInfoNotFound() throws Exception {
        JobInfoUpdateRequest request = JobInfoUpdateRequest.builder()
            .position("Updated Position")
            .build();

        mockMvc.perform(put("/api/jobs/{id}", 9999)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("招聘信息不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetJobInfo_Success() throws Exception {
        JobInfo jobInfo = jobInfoRepository.findAll().get(0);

        mockMvc.perform(get("/api/jobs/{id}", jobInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.id").value(jobInfo.getId()))
            .andExpect(jsonPath("$.data.company.id").value(jobInfo.getCompany().getId()))
            .andExpect(jsonPath("$.data.company.name").value(jobInfo.getCompany().getName()))
            .andExpect(jsonPath("$.data.company.type").value(jobInfo.getCompany().getType().name()))
            .andExpect(jsonPath("$.data.recruit_type").value(jobInfo.getRecruitType().name()))
            .andExpect(jsonPath("$.data.position").value(jobInfo.getPosition()))
            .andExpect(jsonPath("$.data.link").value(jobInfo.getLink()))
            .andExpect(jsonPath("$.data.location").value(jobInfo.getLocation()))
            .andExpect(jsonPath("$.data.extra").value(jobInfo.getExtra()));
    }

    @Test
    void testGetJobInfo_NotFound() throws Exception {
        mockMvc.perform(get("/api/jobs/{id}", 9999))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("招聘信息不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllJobInfos() throws Exception {
        mockMvc.perform(get("/api/jobs")
            .param("page", "1")
            .param("page_size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(3)))
            .andExpect(jsonPath("$.data.total").value(3))
            .andExpect(jsonPath("$.data.count").value(3))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.page_size").value(5))
            .andExpect(jsonPath("$.data.total_pages").value(1));
    }

    @Test
    @SuppressWarnings("null")
    void testSearchJobInfos() throws Exception {
        mockMvc.perform(get("/api/jobs/search")
            .param("keyword", "1")
            .param("recruitType", "INTERN")
            .param("page", "1")
            .param("page_size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].position").value("Position 2"))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.count").value(1))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.page_size").value(5))
            .andExpect(jsonPath("$.data.total_pages").value(1));
    }
}
