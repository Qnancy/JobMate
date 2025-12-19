package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.dto.job.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import cn.edu.zju.cs.jobmate.testing.ControllerTestStartUp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = JobInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = MonitorProperties.class)
class JobInfoControllerTest extends ControllerTestStartUp {

    @MockitoBean
    private JobInfoService jobInfoService;

    @Test
    @SuppressWarnings("null")
    void testCreateJobInfo() throws Exception {
        JobInfoCreateRequest request = JobInfoCreateRequest.builder()
            .companyId(1)
            .recruitType(RecruitType.CAMPUS)
            .position("Java Developer")
            .link("https://job.example.com")
            .location("Hangzhou")
            .extra(null)
            .build();

        Company company = new Company("Test Company", CompanyType.FOREIGN);
        JobInfo jobInfo = request.toModel();
        jobInfo.setCompany(company);

        when(jobInfoService.create(any(JobInfoCreateRequest.class))).thenReturn(jobInfo);

        mockMvc.perform(post("/api/jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.company.name").value("Test Company"))
            .andExpect(jsonPath("$.data.company.type").value("FOREIGN"))
            .andExpect(jsonPath("$.data.position").value("Java Developer"))
            .andExpect(jsonPath("$.data.location").value("Hangzhou"))
            .andExpect(jsonPath("$.data.extra").isEmpty());
    }

    @Test
    void testDeleteJobInfo() throws Exception {
        doNothing().when(jobInfoService).delete(1);

        mockMvc.perform(delete("/api/jobs/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateJobInfo() throws Exception {
        JobInfoUpdateRequest request = JobInfoUpdateRequest.builder()
            .position("New Position")
            .location("New York")
            .build();

        Company company = new Company("Test Company", CompanyType.FOREIGN);
        JobInfo jobInfo = new JobInfo(
            RecruitType.CAMPUS,
            "New Position",
            "https://example.com",
            "New York",
            null
        );
        jobInfo.setCompany(company);

        when(jobInfoService.update(eq(1), any(JobInfoUpdateRequest.class))).thenReturn(jobInfo);

        mockMvc.perform(put("/api/jobs/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.position").value("New Position"))
            .andExpect(jsonPath("$.data.link").value("https://example.com"))
            .andExpect(jsonPath("$.data.location").value("New York"));
    }

    @Test
    void testGetJobInfo() throws Exception {
        Company company = new Company("Test Company", null);
        JobInfo jobInfo = new JobInfo(
            RecruitType.CAMPUS,
            "Test Position",
            null,
            "Test City",
            null
        );
        jobInfo.setCompany(company);

        when(jobInfoService.getById(1)).thenReturn(jobInfo);

        mockMvc.perform(get("/api/jobs/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.position").value("Test Position"))
            .andExpect(jsonPath("$.data.location").value("Test City"))
            .andExpect(jsonPath("$.data.extra").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllJobInfos() throws Exception {
        Company company = new Company("Test Company", null);
        JobInfo job1 = new JobInfo(
            RecruitType.CAMPUS,
            "Position 1",
            null,
            "City 1",
            null
        );
        job1.setCompany(company);
        JobInfo job2 = new JobInfo(
            RecruitType.CAMPUS,
            "Position 2",
            null,
            "City 2",
            "null"
        );

        Page<JobInfo> page = new PageImpl<>(List.of(job1, job2));

        when(jobInfoService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/jobs")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content[0].position").value("Position 1"))
            .andExpect(jsonPath("$.data.content[1].position").value("Position 2"));
    }

    @Test
    @SuppressWarnings("null")
    void testSearchJobInfos() throws Exception {
        Company company = new Company("Test Company", null);
        JobInfo jobInfo = new JobInfo(
            RecruitType.CAMPUS,
            "Test Position",
            null,
            "Test City",
            null
        );
        jobInfo.setCompany(company);

        Page<JobInfo> page = new PageImpl<>(List.of(jobInfo));

        when(jobInfoService.query(any(JobInfoQueryRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/jobs/search")
            .param("keyword", "Java")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content[0].position").value("Test Position"))
            .andExpect(jsonPath("$.data.content[0].location").value("Test City"))
            .andExpect(jsonPath("$.data.content[0].extra").isEmpty());
    }
}
