package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.services.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.annotation.Commit;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CompanyController功能测试")
class CompanyControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 清理数据 - 先删除子表，再删除父表（避免外键约束冲突）
        jobInfoRepository.deleteAll();
        activityInfoRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("测试创建公司 - 成功")
    void testCreateCompany_Success() throws Exception {
        System.out.println("\n========== 测试创建公司 - 成功 ==========");
        
        String requestBody = """
            {
                "name": "测试公司",
                "type": "PRIVATE"
            }
            """;

        mockMvc.perform(post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("公司创建成功"))
                .andExpect(jsonPath("$.data.name").value("测试公司"))
                .andExpect(jsonPath("$.data.type").value("PRIVATE"))
                .andExpect(jsonPath("$.data.id").exists());

        // 验证数据库中确实创建了公司
        assertTrue(companyRepository.existsByName("测试公司"));
        System.out.println("✓ 公司创建成功并保存到数据库");
    }

    @Test
    @DisplayName("测试创建公司 - 重复名称失败")
    void testCreateCompany_DuplicateName() throws Exception {
        System.out.println("\n========== 测试创建公司 - 重复名称失败 ==========");
        
        // 先创建一个公司
        Company existingCompany = new Company("重复公司", CompanyType.PRIVATE);
        companyRepository.save(existingCompany);

        String requestBody = """
            {
                "name": "重复公司",
                "type": "STATE"
            }
            """;

        mockMvc.perform(post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));

        // 验证数据库中只有一个公司
        assertEquals(1, companyRepository.count());
        System.out.println("✓ 重复名称创建失败，符合预期");
    }

    @Test
    @DisplayName("测试创建公司 - 验证失败")
    void testCreateCompany_ValidationFail() throws Exception {
        System.out.println("\n========== 测试创建公司 - 验证失败 ==========");
        
        String requestBody = """
            {
                "name": "",
                "type": "PRIVATE"
            }
            """;

        mockMvc.perform(post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());

        assertEquals(0, companyRepository.count());
        System.out.println("✓ 验证失败，公司未创建");
    }

    @Test
    @DisplayName("测试根据ID获取公司 - 成功")
    void testGetCompanyById_Success() throws Exception {
        System.out.println("\n========== 测试根据ID获取公司 - 成功 ==========");
        
        // 先创建一个公司
        Company company = new Company("查询测试公司", CompanyType.STATE);
        Company savedCompany = companyRepository.save(company);

        mockMvc.perform(get("/api/companies/" + savedCompany.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value(savedCompany.getId()))
                .andExpect(jsonPath("$.data.name").value("查询测试公司"))
                .andExpect(jsonPath("$.data.type").value("STATE"));

        System.out.println("✓ 根据ID查询公司成功");
    }

    @Test
    @DisplayName("测试根据ID获取公司 - 不存在")
    void testGetCompanyById_NotFound() throws Exception {
        System.out.println("\n========== 测试根据ID获取公司 - 不存在 ==========");
        
        mockMvc.perform(get("/api/companies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

        System.out.println("✓ 查询不存在的公司返回404");
    }

    @Test
    @DisplayName("测试分页获取所有公司")
    void testGetAllCompanies() throws Exception {
        System.out.println("\n========== 测试分页获取所有公司 ==========");
        
        // 创建测试数据
        companyRepository.save(new Company("公司1", CompanyType.PRIVATE));
        companyRepository.save(new Company("公司2", CompanyType.STATE));
        companyRepository.save(new Company("公司3", CompanyType.PRIVATE));

        mockMvc.perform(get("/api/companies")
                .param("page", "1")
                .param("page_size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.page_size").value(2))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));

        System.out.println("✓ 分页查询所有公司成功");
    }

    @Test
    @DisplayName("测试按类型筛选公司")
    void testGetCompaniesByType() throws Exception {
        System.out.println("\n========== 测试按类型筛选公司 ==========");
        
        // 创建不同类型的公司
        companyRepository.save(new Company("私企1", CompanyType.PRIVATE));
        companyRepository.save(new Company("国企1", CompanyType.STATE));
        companyRepository.save(new Company("私企2", CompanyType.PRIVATE));

        mockMvc.perform(get("/api/companies")
                .param("type", "PRIVATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.content[0].type").value("PRIVATE"))
                .andExpect(jsonPath("$.data.content[1].type").value("PRIVATE"));

        System.out.println("✓ 按类型筛选公司成功");
    }

    @Test
    @DisplayName("测试更新公司 - 成功")
    void testUpdateCompany_Success() throws Exception {
        System.out.println("\n========== 测试更新公司 - 成功 ==========");
        
        // 先创建一个公司
        Company company = new Company("原始公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        String requestBody = """
            {
                "name": "更新后公司",
                "type": "STATE"
            }
            """;

        mockMvc.perform(put("/api/companies/" + savedCompany.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.id").value(savedCompany.getId()))
                .andExpect(jsonPath("$.data.name").value("更新后公司"))
                .andExpect(jsonPath("$.data.type").value("STATE"));

        // 验证数据库中的数据确实更新了
        Company updatedCompany = companyRepository.findById(savedCompany.getId()).orElse(null);
        assertNotNull(updatedCompany);
        assertEquals("更新后公司", updatedCompany.getName());
        assertEquals(CompanyType.STATE, updatedCompany.getType());
        System.out.println("✓ 公司更新成功");
    }

    @Test
    @DisplayName("测试更新公司 - 不存在")
    void testUpdateCompany_NotFound() throws Exception {
        System.out.println("\n========== 测试更新公司 - 不存在 ==========");
        
        String requestBody = """
            {
                "name": "更新后公司",
                "type": "STATE"
            }
            """;

        mockMvc.perform(put("/api/companies/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

        System.out.println("✓ 更新不存在的公司返回404");
    }

    @Test
    @DisplayName("测试删除公司 - 成功")
    void testDeleteCompany_Success() throws Exception {
        System.out.println("\n========== 测试删除公司 - 成功 ==========");
        
        // 先创建一个公司
        Company company = new Company("待删除公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        mockMvc.perform(delete("/api/companies/" + savedCompany.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"))
                .andExpect(jsonPath("$.data").isEmpty());

        // 验证数据库中的数据确实删除了
        assertFalse(companyRepository.existsById(savedCompany.getId()));
        System.out.println("✓ 公司删除成功");
    }

    @Test
    @DisplayName("测试删除公司 - 不存在")
    void testDeleteCompany_NotFound() throws Exception {
        System.out.println("\n========== 测试删除公司 - 不存在 ==========");
        
        mockMvc.perform(delete("/api/companies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

        System.out.println("✓ 删除不存在的公司返回404");
    }

    @Test
    @DisplayName("测试参数验证 - 负数ID")
    void testInvalidParameters() throws Exception {
        System.out.println("\n========== 测试参数验证 - 负数ID ==========");
        
        mockMvc.perform(get("/api/companies/-1"))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 负数ID验证失败，符合预期");
    }
}