package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.*;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.JobSubscriptionRepository;
import cn.edu.zju.cs.jobmate.repositories.ActivitySubscriptionRepository;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;
import cn.edu.zju.cs.jobmate.services.JobInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("JobInfoController功能测试")
class JobInfoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobSubscriptionRepository jobSubscriptionRepository;

    @Autowired
    private ActivitySubscriptionRepository activitySubscriptionRepository;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 创建测试公司
        testCompany = new Company("测试公司", CompanyType.PRIVATE);
        testCompany = companyRepository.save(testCompany);
    }

    @Test
    @DisplayName("测试创建招聘信息 - 成功")
    void testCreateJobInfo_Success() throws Exception {
        System.out.println("\n========== 测试创建招聘信息 - 成功 ==========");
        
        String requestBody = String.format("""
            {
                "company": {
                    "id": %d,
                    "name": "%s",
                    "type": "%s"
                },
                "recruitType": "INTERN",
                "position": "Java开发工程师",
                "link": "https://example.com/job",
                "location": "北京市海淀区",
                "extra": "实习生岗位"
            }
            """, testCompany.getId(), testCompany.getName(), testCompany.getType());

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("招聘信息创建成功"))
                .andExpect(jsonPath("$.data.company.id").value(testCompany.getId()))
                .andExpect(jsonPath("$.data.recruitType").value("INTERN"))
                .andExpect(jsonPath("$.data.position").value("Java开发工程师"))
                .andExpect(jsonPath("$.data.link").value("https://example.com/job"))
                .andExpect(jsonPath("$.data.location").value("北京市海淀区"))
                .andExpect(jsonPath("$.data.extra").value("实习生岗位"))
                .andExpect(jsonPath("$.data.id").exists());

        // 验证数据库中确实创建了招聘信息
        assertEquals(1, jobInfoRepository.count());
        JobInfo savedJob = jobInfoRepository.findAll().get(0);
        assertEquals("Java开发工程师", savedJob.getPosition());
        assertEquals(testCompany.getId(), savedJob.getCompany().getId());
        System.out.println("✓ 招聘信息创建成功并保存到数据库");
    }

    @Test
    @DisplayName("测试创建招聘信息 - 公司不存在")
    void testCreateJobInfo_CompanyNotFound() throws Exception {
        System.out.println("\n========== 测试创建招聘信息 - 公司不存在 ==========");
        
        String requestBody = """
            {
                "company": {
                    "id": 999,
                    "name": "不存在的公司",
                    "type": "PRIVATE"
                },
                "recruitType": "INTERN",
                "position": "Java开发工程师",
                "link": "https://example.com/job",
                "location": "北京市海淀区",
                "extra": "实习生岗位"
            }
            """;

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("公司不存在"));

        assertEquals(0, jobInfoRepository.count());
        System.out.println("✓ 公司不存在时创建失败，符合预期");
    }

    @Test
    @DisplayName("测试根据ID获取招聘信息 - 成功")
    void testGetJobInfoById_Success() throws Exception {
        System.out.println("\n========== 测试根据ID获取招聘信息 - 成功 ==========");
        
        // 先创建一个招聘信息
        JobInfo jobInfo = new JobInfo(testCompany, RecruitType.CAMPUS, "前端开发", "https://example.com/job");
        jobInfo.setLocation("上海市浦东区");
        jobInfo.setExtra("校园招聘");
        JobInfo savedJobInfo = jobInfoRepository.save(jobInfo);

        mockMvc.perform(get("/api/jobs/" + savedJobInfo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value(savedJobInfo.getId()))
                .andExpect(jsonPath("$.data.position").value("前端开发"))
                .andExpect(jsonPath("$.data.recruitType").value("CAMPUS"))
                .andExpect(jsonPath("$.data.location").value("上海市浦东区"))
                .andExpect(jsonPath("$.data.extra").value("校园招聘"));

        System.out.println("✓ 根据ID查询招聘信息成功");
    }

    @Test
    @DisplayName("测试根据ID获取招聘信息 - 不存在")
    void testGetJobInfoById_NotFound() throws Exception {
        System.out.println("\n========== 测试根据ID获取招聘信息 - 不存在 ==========");
        
        mockMvc.perform(get("/api/jobs/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("招聘信息不存在"));

        System.out.println("✓ 查询不存在的招聘信息返回404");
    }

    @Test
    @DisplayName("测试更新招聘信息 - 成功")
    void testUpdateJobInfo_Success() throws Exception {
        System.out.println("\n========== 测试更新招聘信息 - 成功 ==========");
        
        // 先创建一个招聘信息
        JobInfo jobInfo = new JobInfo(testCompany, RecruitType.INTERN, "原始职位", "https://example.com/old");
        jobInfo.setLocation("原始地点");
        jobInfo.setExtra("原始备注");
        JobInfo savedJobInfo = jobInfoRepository.save(jobInfo);

        String requestBody = String.format("""
            {
                "company": {
                    "id": %d,
                    "name": "%s",
                    "type": "%s"
                },
                "recruitType": "EXPERIENCED",
                "position": "高级Java工程师",
                "link": "https://example.com/senior",
                "location": "深圳市南山区",
                "extra": "高级职位"
            }
            """, testCompany.getId(), testCompany.getName(), testCompany.getType());

        mockMvc.perform(put("/api/jobs/" + savedJobInfo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.id").value(savedJobInfo.getId()))
                .andExpect(jsonPath("$.data.position").value("高级Java工程师"))
                .andExpect(jsonPath("$.data.recruitType").value("EXPERIENCED"))
                .andExpect(jsonPath("$.data.location").value("深圳市南山区"))
                .andExpect(jsonPath("$.data.extra").value("高级职位"));

        // 验证数据库中的数据确实更新了
        JobInfo updatedJob = jobInfoRepository.findById(savedJobInfo.getId()).orElse(null);
        assertNotNull(updatedJob);
        assertEquals("高级Java工程师", updatedJob.getPosition());
        assertEquals(RecruitType.EXPERIENCED, updatedJob.getRecruitType());
        System.out.println("✓ 招聘信息更新成功");
    }

    @Test
    @DisplayName("测试更新招聘信息 - 不存在")
    void testUpdateJobInfo_NotFound() throws Exception {
        System.out.println("\n========== 测试更新招聘信息 - 不存在 ==========");
        
        String requestBody = String.format("""
            {
                "company": {
                    "id": %d,
                    "name": "%s",
                    "type": "%s"
                },
                "recruitType": "EXPERIENCED",
                "position": "高级Java工程师",
                "link": "https://example.com/senior",
                "location": "深圳市南山区",
                "extra": "高级职位"
            }
            """, testCompany.getId(), testCompany.getName(), testCompany.getType());

        mockMvc.perform(put("/api/jobs/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("招聘信息不存在"));

        System.out.println("✓ 更新不存在的招聘信息返回404");
    }

    @Test
    @DisplayName("测试删除招聘信息 - 成功")
    void testDeleteJobInfo_Success() throws Exception {
        System.out.println("\n========== 测试删除招聘信息 - 成功 ==========");
        
        // 先创建一个招聘信息
        JobInfo jobInfo = new JobInfo(testCompany, RecruitType.INTERN, "待删除职位", "https://example.com/delete");
        JobInfo savedJobInfo = jobInfoRepository.save(jobInfo);

        mockMvc.perform(delete("/api/jobs/" + savedJobInfo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"))
                .andExpect(jsonPath("$.data").isEmpty());

        // 验证数据库中的数据确实删除了
        assertFalse(jobInfoRepository.existsById(savedJobInfo.getId()));
        System.out.println("✓ 招聘信息删除成功");
    }

    @Test
    @DisplayName("测试删除招聘信息 - 不存在")
    void testDeleteJobInfo_NotFound() throws Exception {
        System.out.println("\n========== 测试删除招聘信息 - 不存在 ==========");
        
        mockMvc.perform(delete("/api/jobs/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("招聘信息不存在"));

        System.out.println("✓ 删除不存在的招聘信息返回404");
    }

    @Test
    @DisplayName("测试分页获取所有招聘信息")
    void testGetAllJobInfos() throws Exception {
        System.out.println("\n========== 测试分页获取所有招聘信息 ==========");
        
        // 创建测试数据
        jobInfoRepository.save(new JobInfo(testCompany, RecruitType.INTERN, "实习生1", "https://example.com/1"));
        jobInfoRepository.save(new JobInfo(testCompany, RecruitType.CAMPUS, "校招1", "https://example.com/2"));
        jobInfoRepository.save(new JobInfo(testCompany, RecruitType.EXPERIENCED, "社招1", "https://example.com/3"));

        mockMvc.perform(get("/api/jobs")
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

        System.out.println("✓ 分页查询所有招聘信息成功");
    }

    @Test
    @DisplayName("测试搜索招聘信息 - 按关键字")
    void testSearchJobInfos_ByKeyword() throws Exception {
        System.out.println("\n========== 测试搜索招聘信息 - 按关键字 ==========");
        
        // 创建测试数据
        JobInfo job1 = new JobInfo(testCompany, RecruitType.INTERN, "Java开发工程师", "https://example.com/java");
        job1.setLocation("北京");
        jobInfoRepository.save(job1);
        
        JobInfo job2 = new JobInfo(testCompany, RecruitType.CAMPUS, "Python工程师", "https://example.com/python");
        job2.setLocation("上海");
        jobInfoRepository.save(job2);

        // 搜索Java相关职位
        mockMvc.perform(get("/api/jobs/search")
                .param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.content[0].position").value("Java开发工程师"));

        System.out.println("✓ 按关键字搜索招聘信息成功");
    }

    @Test
    @DisplayName("测试搜索招聘信息 - 按招聘类型")
    void testSearchJobInfos_ByRecruitType() throws Exception {
        System.out.println("\n========== 测试搜索招聘信息 - 按招聘类型 ==========");
        
        // 创建测试数据
        jobInfoRepository.save(new JobInfo(testCompany, RecruitType.INTERN, "实习生1", "https://example.com/1"));
        jobInfoRepository.save(new JobInfo(testCompany, RecruitType.INTERN, "实习生2", "https://example.com/2"));
        jobInfoRepository.save(new JobInfo(testCompany, RecruitType.CAMPUS, "校招1", "https://example.com/3"));

        // 搜索实习生职位
        mockMvc.perform(get("/api/jobs/search")
                .param("recruit_type", "INTERN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.content[0].recruitType").value("INTERN"))
                .andExpect(jsonPath("$.data.content[1].recruitType").value("INTERN"));

        System.out.println("✓ 按招聘类型搜索招聘信息成功");
    }

    @Test
    @DisplayName("测试搜索招聘信息 - 组合条件")
    void testSearchJobInfos_Combined() throws Exception {
        System.out.println("\n========== 测试搜索招聘信息 - 组合条件 ==========");
        
        // 创建测试数据
        JobInfo job1 = new JobInfo(testCompany, RecruitType.INTERN, "Java实习生", "https://example.com/1");
        jobInfoRepository.save(job1);
        
        JobInfo job2 = new JobInfo(testCompany, RecruitType.CAMPUS, "Java工程师", "https://example.com/2");
        jobInfoRepository.save(job2);

        // 搜索Java实习生职位
        mockMvc.perform(get("/api/jobs/search")
                .param("keyword", "Java")
                .param("recruit_type", "INTERN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.content[0].position").value("Java实习生"))
                .andExpect(jsonPath("$.data.content[0].recruitType").value("INTERN"));

        System.out.println("✓ 组合条件搜索招聘信息成功");
    }

    @Test
    @DisplayName("测试参数验证")
    void testParameterValidation() throws Exception {
        System.out.println("\n========== 测试参数验证 ==========");
        
        // 测试缺少必填字段
        String invalidRequestBody = """
            {
                "company": {
                    "id": 1,
                    "name": "测试公司",
                    "type": "PRIVATE"
                },
                "position": "",
                "link": "https://example.com/job"
            }
            """;

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 参数验证正常工作");
    }
}