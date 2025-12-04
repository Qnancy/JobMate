package cn.edu.zju.cs.jobmate;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ActiveProfiles("test")
// 暂时移除@Transactional来测试updateById是否工作
public class DebugTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CompanyRepository companyRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        companyRepository.deleteAll();
    }

    @Test
    void debugCreateCompany() throws Exception {
        System.out.println("========== Debug Create Test ==========");
        
        String requestBody = """
            {
                "name": "测试公司",
                "type": "PRIVATE"
            }
            """;

        try {
            MvcResult result = mockMvc.perform(post("/api/companies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andReturn();

            System.out.println("Create Status: " + result.getResponse().getStatus());
            System.out.println("Create Content: " + result.getResponse().getContentAsString());
            
        } catch (Exception e) {
            System.out.println("Create exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void debugUpdateCompany() throws Exception {
        System.out.println("========== Debug Update Test ==========");
        
        // 先创建一个公司
        Company company = new Company("原始公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);
        System.out.println("Created company with ID: " + savedCompany.getId() + ", name: " + savedCompany.getName());

        String updateRequestBody = """
            {
                "name": "更新后公司",
                "type": "STATE"
            }
            """;

        try {
            MvcResult result = mockMvc.perform(put("/api/companies/" + savedCompany.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateRequestBody))
                    .andReturn();

            System.out.println("Update Status: " + result.getResponse().getStatus());
            System.out.println("Update Content: " + result.getResponse().getContentAsString());
            
            // 验证数据库中的实际数据
            Company updatedCompany = companyRepository.findById(savedCompany.getId()).orElse(null);
            if (updatedCompany != null) {
                System.out.println("Database company name: " + updatedCompany.getName());
                System.out.println("Database company type: " + updatedCompany.getType());
            } else {
                System.out.println("Company not found in database!");
            }
            
            if (result.getResolvedException() != null) {
                System.out.println("Update Exception: " + result.getResolvedException().getMessage());
                result.getResolvedException().printStackTrace();
            }
            
        } catch (Exception e) {
            System.out.println("Update exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}