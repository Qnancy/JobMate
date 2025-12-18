package cn.edu.zju.cs.jobmate.integrations;

import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CompanyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        companyRepository.save(new Company("Company1", CompanyType.PRIVATE));
        companyRepository.save(new Company("Company2", CompanyType.STATE));
    }

    @AfterEach
    void clean() {
        companyRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("null")
    void testCreateCompany_Success() throws Exception {
        CompanyCreateRequest request = CompanyCreateRequest.builder()
            .name("Company3")
            .type(CompanyType.PRIVATE)
            .build();

        mockMvc.perform(post("/api/companies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.name").value("Company3"))
            .andExpect(jsonPath("$.data.type").value("PRIVATE"));
    }

    @Test
    @SuppressWarnings("null")
    void testCreateCompany_AlreadyExist() throws Exception {
        CompanyCreateRequest request = CompanyCreateRequest.builder()
            .name("Company1")
            .type(CompanyType.PRIVATE)
            .build();

        mockMvc.perform(post("/api/companies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(409))
            .andExpect(jsonPath("$.message").value("企业已存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteCompany() throws Exception {
        Company company = companyRepository.save(
            new Company("Company3", CompanyType.PRIVATE));

        mockMvc.perform(delete("/api/companies/" + company.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateCompany_Success() throws Exception {
        Company company = companyRepository.save(
            new Company("OldCompany", CompanyType.PRIVATE));

        CompanyUpdateRequest request = CompanyUpdateRequest.builder()
            .name("NewCompany")
            .type(null)
            .build();

        mockMvc.perform(put("/api/companies/" + company.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.name").value("NewCompany"))
            .andExpect(jsonPath("$.data.type").value("PRIVATE"));
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateCompany_NoUpdate() throws Exception {
        Company company = companyRepository.save(
            new Company("SomeCompany", CompanyType.PRIVATE));

        CompanyUpdateRequest request = CompanyUpdateRequest.builder()
            .name(null)
            .type(null)
            .build();

        mockMvc.perform(put("/api/companies/" + company.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("没有更新"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateCompany_NotFound() throws Exception {
        CompanyUpdateRequest request = CompanyUpdateRequest.builder()
            .name("NewName")
            .type(CompanyType.STATE)
            .build();

        mockMvc.perform(put("/api/companies/9999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("企业不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetCompany_Success() throws Exception {
        Company company = companyRepository.save(
            new Company("Company3", CompanyType.STATE));

        mockMvc.perform(get("/api/companies/" + company.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.id").value(company.getId()))
            .andExpect(jsonPath("$.data.name").value("Company3"))
            .andExpect(jsonPath("$.data.type").value("STATE"));
    }

    @Test
    void testGetCompany_NotFound() throws Exception {
        mockMvc.perform(get("/api/companies/9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("企业不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllCompanies() throws Exception {
        for (int i = 3; i <= 10; i++) {
            companyRepository.save(
                new Company("Company" + i, CompanyType.PRIVATE));
        }

        // Case 1.
        mockMvc.perform(get("/api/companies")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(10)))
            .andExpect(jsonPath("$.data.total").value(10))
            .andExpect(jsonPath("$.data.count").value(10))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.page_size").value(10))
            .andExpect(jsonPath("$.data.total_pages").value(1));
        // Case 2.
        mockMvc.perform(get("/api/companies")
            .param("page", "3")
            .param("page_size", "4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(2)))
            .andExpect(jsonPath("$.data.total").value(10))
            .andExpect(jsonPath("$.data.count").value(2))
            .andExpect(jsonPath("$.data.page").value(3))
            .andExpect(jsonPath("$.data.page_size").value(4))
            .andExpect(jsonPath("$.data.total_pages").value(3));
    }

    @Test
    @SuppressWarnings("null")
    void testGetSearchCompanies() throws Exception {
        companyRepository.save(new Company("TechCorp", CompanyType.PRIVATE));
        companyRepository.save(new Company("HealthTech", CompanyType.STATE));
        companyRepository.save(new Company("EduSoft", CompanyType.PRIVATE));

        // Keyword search.
        mockMvc.perform(get("/api/companies/search")
            .param("keyword", "Tech")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(2)))
            .andExpect(jsonPath("$.data.content[0].name", containsString("Tech")))
            .andExpect(jsonPath("$.data.content[1].name", containsString("Tech")))
            .andExpect(jsonPath("$.data.total").value(2))
            .andExpect(jsonPath("$.data.count").value(2))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.page_size").value(10))
            .andExpect(jsonPath("$.data.total_pages").value(1));
        
        // Filter by type.
        mockMvc.perform(get("/api/companies/search")
            .param("type", "PRIVATE")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(3)))
            .andExpect(jsonPath("$.data.content[0].type").value("PRIVATE"))
            .andExpect(jsonPath("$.data.content[1].type").value("PRIVATE"))
            .andExpect(jsonPath("$.data.content[2].type").value("PRIVATE"))
            .andExpect(jsonPath("$.data.total").value(3))
            .andExpect(jsonPath("$.data.count").value(3))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.page_size").value(10))
            .andExpect(jsonPath("$.data.total_pages").value(1));

        // Combined search.
        mockMvc.perform(get("/api/companies/search")
            .param("keyword", "Tech")
            .param("type", "STATE")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].name", containsString("Tech")))
            .andExpect(jsonPath("$.data.content[0].type").value("STATE"))
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.count").value(1))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.page_size").value(10))
            .andExpect(jsonPath("$.data.total_pages").value(1));
    }
}
