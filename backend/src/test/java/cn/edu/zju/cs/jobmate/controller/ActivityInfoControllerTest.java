package cn.edu.zju.cs.jobmate.controller;

import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.*;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;
import cn.edu.zju.cs.jobmate.repositories.JobSubscriptionRepository;
import cn.edu.zju.cs.jobmate.repositories.ActivitySubscriptionRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.UserRepository;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
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
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ActivityInfoController功能测试")
class ActivityInfoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ActivityInfoService activityInfoService;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobSubscriptionRepository jobSubscriptionRepository;

    @Autowired
    private ActivitySubscriptionRepository activitySubscriptionRepository;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Company testCompany;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 创建测试公司
        testCompany = new Company("测试公司", CompanyType.PRIVATE);
        testCompany = companyRepository.save(testCompany);
    }

    @Test
    @DisplayName("测试创建宣讲会 - 成功")
    void testCreateActivityInfo_Success() throws Exception {
        System.out.println("\n========== 测试创建宣讲会 - 成功 ==========");
        
        LocalDateTime activityTime = LocalDateTime.now().plusDays(7);
        String formattedTime = activityTime.format(formatter);
        
        String requestBody = String.format("""
            {
                "company": {
                    "id": %d,
                    "name": "%s",
                    "type": "%s"
                },
                "title": "2024校园招聘宣讲会",
                "time": "%s",
                "link": "https://example.com/activity",
                "location": "北京大学英杰交流中心",
                "extra": "欢迎同学们参加"
            }
            """, testCompany.getId(), testCompany.getName(), testCompany.getType(), formattedTime);

        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("宣讲会信息创建成功"))
                .andExpect(jsonPath("$.data.company.id").value(testCompany.getId()))
                .andExpect(jsonPath("$.data.title").value("2024校园招聘宣讲会"))
                .andExpect(jsonPath("$.data.time").value(formattedTime))
                .andExpect(jsonPath("$.data.link").value("https://example.com/activity"))
                .andExpect(jsonPath("$.data.location").value("北京大学英杰交流中心"))
                .andExpect(jsonPath("$.data.extra").value("欢迎同学们参加"))
                .andExpect(jsonPath("$.data.id").exists());

        // 验证数据库中确实创建了宣讲会
        assertEquals(1, activityInfoRepository.count());
        ActivityInfo savedActivity = activityInfoRepository.findAll().get(0);
        assertEquals("2024校园招聘宣讲会", savedActivity.getTitle());
        assertEquals(testCompany.getId(), savedActivity.getCompany().getId());
        System.out.println("✓ 宣讲会创建成功并保存到数据库");
    }

    @Test
    @DisplayName("测试创建宣讲会 - 公司不存在")
    void testCreateActivityInfo_CompanyNotFound() throws Exception {
        System.out.println("\n========== 测试创建宣讲会 - 公司不存在 ==========");
        
        LocalDateTime activityTime = LocalDateTime.now().plusDays(7);
        String formattedTime = activityTime.format(formatter);
        
        String requestBody = String.format("""
            {
                "company": {
                    "id": 999,
                    "name": "不存在的公司",
                    "type": "PRIVATE"
                },
                "title": "测试宣讲会",
                "time": "%s",
                "link": "https://example.com/activity",
                "location": "测试地点",
                "extra": "测试备注"
            }
            """, formattedTime);

        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("公司不存在"));

        assertEquals(0, activityInfoRepository.count());
        System.out.println("✓ 公司不存在时创建失败，符合预期");
    }

    @Test
    @DisplayName("测试根据ID获取宣讲会 - 成功")
    void testGetActivityInfoById_Success() throws Exception {
        System.out.println("\n========== 测试根据ID获取宣讲会 - 成功 ==========");
        
        // 先创建一个宣讲会
        LocalDateTime activityTime = LocalDateTime.now().plusDays(10);
        ActivityInfo activityInfo = new ActivityInfo(testCompany, "技术分享会", activityTime);
        activityInfo.setLink("https://example.com/tech");
        activityInfo.setLocation("清华大学主楼");
        activityInfo.setExtra("技术交流活动");
        ActivityInfo savedActivity = activityInfoRepository.save(activityInfo);

        mockMvc.perform(get("/api/activities/" + savedActivity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value(savedActivity.getId()))
                .andExpect(jsonPath("$.data.title").value("技术分享会"))
                .andExpect(jsonPath("$.data.link").value("https://example.com/tech"))
                .andExpect(jsonPath("$.data.location").value("清华大学主楼"))
                .andExpect(jsonPath("$.data.extra").value("技术交流活动"));

        System.out.println("✓ 根据ID查询宣讲会成功");
    }

    @Test
    @DisplayName("测试根据ID获取宣讲会 - 不存在")
    void testGetActivityInfoById_NotFound() throws Exception {
        System.out.println("\n========== 测试根据ID获取宣讲会 - 不存在 ==========");
        
        mockMvc.perform(get("/api/activities/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("活动信息不存在"));

        System.out.println("✓ 查询不存在的宣讲会返回404");
    }

    @Test
    @DisplayName("测试更新宣讲会 - 成功")
    void testUpdateActivityInfo_Success() throws Exception {
        System.out.println("\n========== 测试更新宣讲会 - 成功 ==========");
        
        // 先创建一个宣讲会
        LocalDateTime originalTime = LocalDateTime.now().plusDays(5);
        ActivityInfo activityInfo = new ActivityInfo(testCompany, "原始标题", originalTime);
        activityInfo.setLink("https://example.com/old");
        activityInfo.setLocation("原始地点");
        activityInfo.setExtra("原始备注");
        ActivityInfo savedActivity = activityInfoRepository.save(activityInfo);

        LocalDateTime newTime = LocalDateTime.now().plusDays(15);
        String formattedNewTime = newTime.format(formatter);
        
        String requestBody = String.format("""
            {
                "company": {
                    "id": %d,
                    "name": "%s",
                    "type": "%s"
                },
                "title": "更新后的宣讲会",
                "time": "%s",
                "link": "https://example.com/updated",
                "location": "更新后地点",
                "extra": "更新后备注"
            }
            """, testCompany.getId(), testCompany.getName(), testCompany.getType(), formattedNewTime);

        mockMvc.perform(put("/api/activities/" + savedActivity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.id").value(savedActivity.getId()))
                .andExpect(jsonPath("$.data.title").value("更新后的宣讲会"))
                .andExpect(jsonPath("$.data.time").value(formattedNewTime))
                .andExpect(jsonPath("$.data.link").value("https://example.com/updated"))
                .andExpect(jsonPath("$.data.location").value("更新后地点"))
                .andExpect(jsonPath("$.data.extra").value("更新后备注"));

        // 验证数据库中的数据确实更新了
        ActivityInfo updatedActivity = activityInfoRepository.findById(savedActivity.getId()).orElse(null);
        assertNotNull(updatedActivity);
        assertEquals("更新后的宣讲会", updatedActivity.getTitle());
        assertEquals("更新后地点", updatedActivity.getLocation());
        System.out.println("✓ 宣讲会更新成功");
    }

    @Test
    @DisplayName("测试更新宣讲会 - 不存在")
    void testUpdateActivityInfo_NotFound() throws Exception {
        System.out.println("\n========== 测试更新宣讲会 - 不存在 ==========");
        
        LocalDateTime activityTime = LocalDateTime.now().plusDays(7);
        String formattedTime = activityTime.format(formatter);
        
        String requestBody = String.format("""
            {
                "company": {
                    "id": %d,
                    "name": "%s",
                    "type": "%s"
                },
                "title": "更新后的宣讲会",
                "time": "%s",
                "link": "https://example.com/updated",
                "location": "更新后地点",
                "extra": "更新后备注"
            }
            """, testCompany.getId(), testCompany.getName(), testCompany.getType(), formattedTime);

        mockMvc.perform(put("/api/activities/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("活动信息不存在"));

        System.out.println("✓ 更新不存在的宣讲会返回404");
    }

    @Test
    @DisplayName("测试删除宣讲会 - 成功")
    void testDeleteActivityInfo_Success() throws Exception {
        System.out.println("\n========== 测试删除宣讲会 - 成功 ==========");
        
        // 先创建一个宣讲会
        LocalDateTime activityTime = LocalDateTime.now().plusDays(3);
        ActivityInfo activityInfo = new ActivityInfo(testCompany, "待删除宣讲会", activityTime);
        ActivityInfo savedActivity = activityInfoRepository.save(activityInfo);

        mockMvc.perform(delete("/api/activities/" + savedActivity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"))
                .andExpect(jsonPath("$.data").isEmpty());

        // 验证数据库中的数据确实删除了
        assertFalse(activityInfoRepository.existsById(savedActivity.getId()));
        System.out.println("✓ 宣讲会删除成功");
    }

    @Test
    @DisplayName("测试删除宣讲会 - 不存在")
    void testDeleteActivityInfo_NotFound() throws Exception {
        System.out.println("\n========== 测试删除宣讲会 - 不存在 ==========");
        
        mockMvc.perform(delete("/api/activities/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("活动信息不存在"));

        System.out.println("✓ 删除不存在的宣讲会返回404");
    }

    @Test
    @DisplayName("测试分页获取所有宣讲会")
    void testGetAllActivityInfos() throws Exception {
        System.out.println("\n========== 测试分页获取所有宣讲会 ==========");
        
        // 创建测试数据
        LocalDateTime time1 = LocalDateTime.now().plusDays(1);
        LocalDateTime time2 = LocalDateTime.now().plusDays(2);
        LocalDateTime time3 = LocalDateTime.now().plusDays(3);
        
        activityInfoRepository.save(new ActivityInfo(testCompany, "宣讲会1", time1));
        activityInfoRepository.save(new ActivityInfo(testCompany, "宣讲会2", time2));
        activityInfoRepository.save(new ActivityInfo(testCompany, "宣讲会3", time3));

        mockMvc.perform(get("/api/activities")
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

        System.out.println("✓ 分页查询所有宣讲会成功");
    }

    @Test
    @DisplayName("测试搜索宣讲会 - 按关键字")
    void testSearchActivityInfos_ByKeyword() throws Exception {
        System.out.println("\n========== 测试搜索宣讲会 - 按关键字 ==========");
        
        // 创建测试数据
        LocalDateTime time1 = LocalDateTime.now().plusDays(1);
        LocalDateTime time2 = LocalDateTime.now().plusDays(2);
        
        ActivityInfo activity1 = new ActivityInfo(testCompany, "技术分享会", time1);
        activityInfoRepository.save(activity1);
        
        ActivityInfo activity2 = new ActivityInfo(testCompany, "产品发布会", time2);
        activityInfoRepository.save(activity2);

        // 搜索技术相关活动
        mockMvc.perform(get("/api/activities/search")
                .param("keyword", "技术"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("技术分享会"));

        System.out.println("✓ 按关键字搜索宣讲会成功");
    }

    @Test
    @DisplayName("测试搜索宣讲会 - 无结果")
    void testSearchActivityInfos_NoResults() throws Exception {
        System.out.println("\n========== 测试搜索宣讲会 - 无结果 ==========");
        
        // 创建测试数据
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        ActivityInfo activity = new ActivityInfo(testCompany, "普通宣讲会", time);
        activityInfoRepository.save(activity);

        // 搜索不存在的关键字
        mockMvc.perform(get("/api/activities/search")
                .param("keyword", "不存在的关键字"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(0));

        System.out.println("✓ 搜索无结果时正常返回空列表");
    }

    @Test
    @DisplayName("测试参数验证")
    void testParameterValidation() throws Exception {
        System.out.println("\n========== 测试参数验证 ==========");
        
        // 测试缺少必填字段
        String invalidRequestBody = String.format("""
            {
                "company": {
                    "id": %d,
                    "name": "%s",
                    "type": "%s"
                },
                "title": "",
                "time": "%s",
                "link": "https://example.com/activity",
                "location": "测试地点",
                "extra": "测试备注"
            }
            """, testCompany.getId(), testCompany.getName(), testCompany.getType(), 
            LocalDateTime.now().plusDays(7).format(formatter));

        mockMvc.perform(post("/api/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 参数验证正常工作");
    }
}