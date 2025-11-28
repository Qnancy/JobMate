package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ActivitySubscriptionController功能测试")
class ActivitySubscriptionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ActivitySubscriptionService activitySubscriptionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    @Autowired
    private ActivitySubscriptionRepository activitySubscriptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("测试创建活动订阅 - 成功")
    void testCreateActivitySubscription_Success() throws Exception {
        System.out.println("\n========== 测试创建活动订阅 - 成功 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        ActivityInfo activityInfo = new ActivityInfo(savedCompany, "春季宣讲会", LocalDateTime.now().plusDays(7));
        ActivityInfo savedActivityInfo = activityInfoRepository.save(activityInfo);

        String requestBody = String.format("""
            {
                "user": {"id": %d},
                "activityInfo": {"id": %d}
            }
            """, savedUser.getId(), savedActivityInfo.getId());

        mockMvc.perform(post("/api/activitysubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("宣讲会订阅创建成功"))
                .andExpect(jsonPath("$.data.user.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.activityInfo.id").value(savedActivityInfo.getId()))
                .andExpect(jsonPath("$.data.id").exists());

        // 验证数据库中确实创建了订阅
        assertTrue(activitySubscriptionService.existsByUserIdAndActivityInfoId(savedUser.getId(), savedActivityInfo.getId()));
        System.out.println("✓ 活动订阅创建成功");
    }

    @Test
    @DisplayName("测试创建活动订阅 - 用户不存在")
    void testCreateActivitySubscription_UserNotFound() throws Exception {
        System.out.println("\n========== 测试创建活动订阅 - 用户不存在 ==========");
        
        // 只创建活动信息，不创建用户
        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        ActivityInfo activityInfo = new ActivityInfo(savedCompany, "春季宣讲会", LocalDateTime.now().plusDays(7));
        ActivityInfo savedActivityInfo = activityInfoRepository.save(activityInfo);

        String requestBody = String.format("""
            {
                "user": {"id": 999},
                "activityInfo": {"id": %d}
            }
            """, savedActivityInfo.getId());

        mockMvc.perform(post("/api/activitysubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));

        System.out.println("✓ 用户不存在时订阅创建失败");
    }

    @Test
    @DisplayName("测试创建活动订阅 - 活动不存在")
    void testCreateActivitySubscription_ActivityInfoNotFound() throws Exception {
        System.out.println("\n========== 测试创建活动订阅 - 活动不存在 ==========");
        
        // 只创建用户，不创建活动信息
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        String requestBody = String.format("""
            {
                "user": {"id": %d},
                "activityInfo": {"id": 999}
            }
            """, savedUser.getId());

        mockMvc.perform(post("/api/activitysubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("活动信息不存在"));

        System.out.println("✓ 活动不存在时订阅创建失败");
    }

    @Test
    @DisplayName("测试创建活动订阅 - 重复订阅")
    void testCreateActivitySubscription_AlreadyExists() throws Exception {
        System.out.println("\n========== 测试创建活动订阅 - 重复订阅 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        ActivityInfo activityInfo = new ActivityInfo(savedCompany, "春季宣讲会", LocalDateTime.now().plusDays(7));
        ActivityInfo savedActivityInfo = activityInfoRepository.save(activityInfo);

        // 先创建一个订阅
        activitySubscriptionService.create(savedUser, savedActivityInfo);

        String requestBody = String.format("""
            {
                "user": {"id": %d},
                "activityInfo": {"id": %d}
            }
            """, savedUser.getId(), savedActivityInfo.getId());

        mockMvc.perform(post("/api/activitysubscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("活动订阅已存在"));

        System.out.println("✓ 重复订阅时创建失败");
    }

    @Test
    @DisplayName("测试删除活动订阅 - 成功")
    void testDeleteActivitySubscription_Success() throws Exception {
        System.out.println("\n========== 测试删除活动订阅 - 成功 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        ActivityInfo activityInfo = new ActivityInfo(savedCompany, "春季宣讲会", LocalDateTime.now().plusDays(7));
        ActivityInfo savedActivityInfo = activityInfoRepository.save(activityInfo);

        ActivitySubscription subscription = activitySubscriptionService.create(savedUser, savedActivityInfo);

        mockMvc.perform(delete("/api/activitysubscriptions/" + subscription.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));

        // 验证数据库中的订阅确实删除了
        assertFalse(activitySubscriptionService.existsById(subscription.getId()));
        System.out.println("✓ 活动订阅删除成功");
    }

    @Test
    @DisplayName("测试删除活动订阅 - 不存在")
    void testDeleteActivitySubscription_NotFound() throws Exception {
        System.out.println("\n========== 测试删除活动订阅 - 不存在 ==========");
        
        mockMvc.perform(delete("/api/activitysubscriptions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("活动订阅不存在"));

        System.out.println("✓ 删除不存在的订阅返回404");
    }

    @Test
    @DisplayName("测试分页查询活动订阅")
    void testGetAllActivitySubscriptions() throws Exception {
        System.out.println("\n========== 测试分页查询活动订阅 ==========");
        
        // 创建测试数据
        User user = new User("testuser", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Company company = new Company("测试公司", CompanyType.PRIVATE);
        Company savedCompany = companyRepository.save(company);

        ActivityInfo activity1 = new ActivityInfo(savedCompany, "春季宣讲会", LocalDateTime.now().plusDays(7));
        ActivityInfo savedActivity1 = activityInfoRepository.save(activity1);

        ActivityInfo activity2 = new ActivityInfo(savedCompany, "秋季宣讲会", LocalDateTime.now().plusDays(14));
        ActivityInfo savedActivity2 = activityInfoRepository.save(activity2);

        activitySubscriptionService.create(savedUser, savedActivity1);
        activitySubscriptionService.create(savedUser, savedActivity2);

        mockMvc.perform(get("/api/activitysubscriptions")
                .param("page", "1")
                .param("page_size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2));

        System.out.println("✓ 分页查询活动订阅成功");
    }

    @Test
    @DisplayName("测试参数验证 - 负数ID")
    void testInvalidParameters() throws Exception {
        System.out.println("\n========== 测试参数验证 - 负数ID ==========");
        
        mockMvc.perform(delete("/api/activitysubscriptions/-1"))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 负数ID验证失败，符合预期");
    }
}