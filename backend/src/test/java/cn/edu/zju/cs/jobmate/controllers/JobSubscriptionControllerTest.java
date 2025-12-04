package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.enums.UserRole;
import cn.edu.zju.cs.jobmate.models.*;
import cn.edu.zju.cs.jobmate.repositories.*;
import cn.edu.zju.cs.jobmate.services.*;
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
@DisplayName("JobSubscriptionController功能测试")
class JobSubscriptionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JobSubscriptionService jobSubscriptionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("测试创建职位订阅 - 成功")
    void testCreateJobSubscription_Success() throws Exception {
        System.out.println("\n========== 测试创建职位订阅 - 成功 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        JobInfo jobInfo = new JobInfo(savedCompany, RecruitType.INTERN, "Java开发", "http://test.com");
        JobInfo savedJobInfo = jobInfoRepository.save(jobInfo);

        String requestBody = String.format("""
            {
                "user": {"id": %d},
                "jobInfo": {"id": %d}
            }
            """, savedUser.getId(), savedJobInfo.getId());

        mockMvc.perform(post("/api/jobsubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("招聘订阅创建成功"))
                .andExpect(jsonPath("$.data.user.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.jobInfo.id").value(savedJobInfo.getId()))
                .andExpect(jsonPath("$.data.id").exists());

        // 验证数据库中确实创建了订阅
        assertTrue(jobSubscriptionService.existsByUserIdAndJobInfoId(savedUser.getId(), savedJobInfo.getId()));
        System.out.println("✓ 职位订阅创建成功");
    }

    @Test
    @DisplayName("测试创建职位订阅 - 用户不存在")
    void testCreateJobSubscription_UserNotFound() throws Exception {
        System.out.println("\n========== 测试创建职位订阅 - 用户不存在 ==========");
        
        // 只创建职位信息，不创建用户
        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        JobInfo jobInfo = new JobInfo(savedCompany, RecruitType.INTERN, "Java开发", "http://test.com");
        JobInfo savedJobInfo = jobInfoRepository.save(jobInfo);

        String requestBody = String.format("""
            {
                "user": {"id": 999},
                "jobInfo": {"id": %d}
            }
            """, savedJobInfo.getId());

        mockMvc.perform(post("/api/jobsubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        System.out.println("✓ 用户不存在时订阅创建失败");
    }

    @Test
    @DisplayName("测试创建职位订阅 - 职位不存在")
    void testCreateJobSubscription_JobInfoNotFound() throws Exception {
        System.out.println("\n========== 测试创建职位订阅 - 职位不存在 ==========");
        
        // 只创建用户，不创建职位信息
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        String requestBody = String.format("""
            {
                "user": {"id": %d},
                "jobInfo": {"id": 999}
            }
            """, savedUser.getId());

        mockMvc.perform(post("/api/jobsubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("招聘信息不存在"));

        System.out.println("✓ 职位不存在时订阅创建失败");
    }

    @Test
    @DisplayName("测试创建职位订阅 - 重复订阅")
    void testCreateJobSubscription_AlreadyExists() throws Exception {
        System.out.println("\n========== 测试创建职位订阅 - 重复订阅 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        JobInfo jobInfo = new JobInfo(savedCompany, RecruitType.INTERN, "Java开发", "http://test.com");
        JobInfo savedJobInfo = jobInfoRepository.save(jobInfo);

        // 先创建一个订阅
        jobSubscriptionService.create(savedUser, savedJobInfo);

        String requestBody = String.format("""
            {
                "user": {"id": %d},
                "jobInfo": {"id": %d}
            }
            """, savedUser.getId(), savedJobInfo.getId());

        mockMvc.perform(post("/api/jobsubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("招聘订阅已存在"));

        System.out.println("✓ 重复订阅时创建失败");
    }

    @Test
    @DisplayName("测试删除职位订阅 - 成功")
    void testDeleteJobSubscription_Success() throws Exception {
        System.out.println("\n========== 测试删除职位订阅 - 成功 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        JobInfo jobInfo = new JobInfo(savedCompany, RecruitType.INTERN, "Java开发", "http://test.com");
        JobInfo savedJobInfo = jobInfoRepository.save(jobInfo);

        JobSubscription subscription = jobSubscriptionService.create(savedUser, savedJobInfo);

        mockMvc.perform(delete("/api/jobsubscriptions/" + subscription.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));

        // 验证数据库中的订阅确实删除了
        assertFalse(jobSubscriptionService.existsById(subscription.getId()));
        System.out.println("✓ 职位订阅删除成功");
    }

    @Test
    @DisplayName("测试删除职位订阅 - 不存在")
    void testDeleteJobSubscription_NotFound() throws Exception {
        System.out.println("\n========== 测试删除职位订阅 - 不存在 ==========");
        
        mockMvc.perform(delete("/api/jobsubscriptions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("招聘订阅不存在"));

        System.out.println("✓ 删除不存在的订阅返回404");
    }

    @Test
    @DisplayName("测试分页查询订阅")
    void testGetAllJobSubscriptions() throws Exception {
        System.out.println("\n========== 测试分页查询订阅 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        JobInfo jobInfo1 = new JobInfo(savedCompany, RecruitType.INTERN, "Java开发", "http://test1.com");
        JobInfo savedJobInfo1 = jobInfoRepository.save(jobInfo1);

        JobInfo jobInfo2 = new JobInfo(savedCompany, RecruitType.CAMPUS, "Python开发", "http://test2.com");
        JobInfo savedJobInfo2 = jobInfoRepository.save(jobInfo2);

        jobSubscriptionService.create(savedUser, savedJobInfo1);
        jobSubscriptionService.create(savedUser, savedJobInfo2);

        mockMvc.perform(get("/api/jobsubscriptions")
                .param("page", "1")
                .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));

        System.out.println("✓ 分页查询订阅成功");
    }

    @Test
    @DisplayName("测试参数验证 - 负数ID")
    void testInvalidParameters() throws Exception {
        System.out.println("\n========== 测试参数验证 - 负数ID ==========");
        
        mockMvc.perform(delete("/api/jobsubscriptions/-1"))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 负数ID验证失败，符合预期");
    }
}