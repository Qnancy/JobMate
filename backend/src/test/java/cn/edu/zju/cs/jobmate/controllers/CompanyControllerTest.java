package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.company.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import cn.edu.zju.cs.jobmate.utils.ControllerTestUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(controllers = CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = MonitorProperties.class)
class CompanyControllerTest extends ControllerTestUtil {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SuppressWarnings("null")
    void testCreateCompany() throws Exception {
        String name = "TestCompany";
        CompanyType type = CompanyType.STATE;
        CompanyCreateRequest request = CompanyCreateRequest.builder()
            .name(name)
            .type(type)
            .build();

        Company company = new Company(name, type);
        when(companyService.create(any(CompanyCreateRequest.class))).thenReturn(company);

        mockMvc.perform(post("/api/companies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.name").value("TestCompany"))
            .andExpect(jsonPath("$.data.type").value("STATE"));
    }

    @Test
    void testDeleteCompany() throws Exception {
        doNothing().when(companyService).delete(1);

        mockMvc.perform(delete("/api/companies/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateCompany() throws Exception {
        CompanyUpdateRequest request = CompanyUpdateRequest.builder()
            .name("UpdatedCompany")
            .type(CompanyType.PRIVATE)
            .build();

        Company company = new Company("UpdatedCompany", CompanyType.PRIVATE);
        when(companyService.update(eq(1), any(CompanyUpdateRequest.class)))
            .thenReturn(company);

        mockMvc.perform(put("/api/companies/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.name").value("UpdatedCompany"))
            .andExpect(jsonPath("$.data.type").value("PRIVATE"));
    }

    @Test
    void testGetCompany() throws Exception {
        Company company = new Company("TestCompany", CompanyType.STATE);
        when(companyService.getById(1)).thenReturn(company);

        mockMvc.perform(get("/api/companies/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.name").value("TestCompany"))
            .andExpect(jsonPath("$.data.type").value("STATE"));
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllCompanies() throws Exception {
        Company company1 = new Company("A", CompanyType.STATE);
        Company company2 = new Company("B", CompanyType.STATE);
        Company company3 = new Company("C", CompanyType.PRIVATE);
        Page<Company> page = new PageImpl<>(List.of(
            company1, company2, company3
        ));

        PageRequest request = PageRequest.builder()
            .page(1)
            .pageSize(10)
            .build();
        when(companyService.getAll(eq(request))).thenReturn(page);

        mockMvc.perform(get("/api/companies")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content[0].name").value("A"))
            .andExpect(jsonPath("$.data.content[1].name").value("B"))
            .andExpect(jsonPath("$.data.content[2].name").value("C"))
            .andExpect(jsonPath("$.data.content[0].type").value("STATE"))
            .andExpect(jsonPath("$.data.content[1].type").value("STATE"))
            .andExpect(jsonPath("$.data.content[2].type").value("PRIVATE"));
    }

    @Test
    @SuppressWarnings("null")
    void testSearchCompanies() throws Exception {
        Company company1 = new Company("AlphaTech", CompanyType.PRIVATE);
        Company company2 = new Company("BetaSolutions", CompanyType.STATE);
        Page<Company> page = new PageImpl<>(List.of(
            company1, company2
        ));

        CompanyQueryRequest request = CompanyQueryRequest.builder()
            .keyword("Tech")
            .type(CompanyType.PRIVATE)
            .page(1)
            .pageSize(10)
            .build();
        when(companyService.query(eq(request))).thenReturn(page);

        mockMvc.perform(get("/api/companies/search")
            .param("keyword", "Tech")
            .param("type", "PRIVATE")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content[0].name").value("AlphaTech"))
            .andExpect(jsonPath("$.data.content[0].type").value("PRIVATE"))
            .andExpect(jsonPath("$.data.content[1].name").value("BetaSolutions"))
            .andExpect(jsonPath("$.data.content[1].type").value("STATE"));
    }
}
