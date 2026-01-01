package cn.edu.zju.cs.jobmate.integrations;

import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoCreateRequest;
import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoUpdateRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.CompanyRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ActivityInfoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Company company1;
    private Company company2;

    @BeforeEach
    void setUp() {
        company1 = companyRepository.save(new Company("Company 1", CompanyType.FOREIGN));
        company2 = companyRepository.save(new Company("Company 2", CompanyType.STATE));

        ActivityInfo activity1 = new ActivityInfo(
            "Activity 1",
            LocalDateTime.now(),
            "https://a1.com",
            "City 1",
            "Extra 1"
        );
        activity1.setCompany(company1);
        activityInfoRepository.save(activity1);

        ActivityInfo activity2 = new ActivityInfo(
            "Activity 2",
            LocalDateTime.now(),
            "https://a2.com",
            "City 2",
            "Extra 2"
        );
        activity2.setCompany(company1);
        activityInfoRepository.save(activity2);

        ActivityInfo activity3 = new ActivityInfo(
            "Activity 3",
            LocalDateTime.now(),
            "https://a3.com",
            "City 3",
            "Extra 3"
        );
        activity3.setCompany(company2);
        activityInfoRepository.save(activity3);
    }

    @AfterEach
    void clean() {
        activityInfoRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateActivityInfo_Success() throws Exception {
        ActivityInfoCreateRequest request = ActivityInfoCreateRequest.builder()
            .companyId(company1.getId())
            .title("Test Activity")
            .time(LocalDateTime.now())
            .link("https://activity.example.com")
            .location("Hangzhou")
            .extra("Extra Info")
            .build();

        mockMvc.perform(post("/api/activities")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.company.id").value(company1.getId()))
            .andExpect(jsonPath("$.data.company.name").value(company1.getName()))
            .andExpect(jsonPath("$.data.company.type").value(company1.getType().name()))
            .andExpect(jsonPath("$.data.title").value("Test Activity"))
            .andExpect(jsonPath("$.data.link").value("https://activity.example.com"))
            .andExpect(jsonPath("$.data.location").value("Hangzhou"))
            .andExpect(jsonPath("$.data.extra").value("Extra Info"));
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateActivityInfo_CompanyNotFound() throws Exception {
        ActivityInfoCreateRequest request = ActivityInfoCreateRequest.builder()
            .companyId(9999L)
            .title("Test Activity")
            .time(LocalDateTime.now())
            .link(null)
            .location(null)
            .extra(null)
            .build();

        mockMvc.perform(post("/api/activities")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("企业不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteActivityInfo() throws Exception {
        ActivityInfo activityInfo = activityInfoRepository.findAll().get(0);

        mockMvc.perform(delete("/api/activities/{id}", activityInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateActivityInfo_Success() throws Exception {
        ActivityInfo activityInfo = activityInfoRepository.findAll().get(0);

        ActivityInfoUpdateRequest request = ActivityInfoUpdateRequest.builder()
            .title("Updated Activity")
            .location("Shanghai")
            .build();

        mockMvc.perform(put("/api/activities/{id}", activityInfo.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.id").value(activityInfo.getId()))
            .andExpect(jsonPath("$.data.title").value("Updated Activity"))
            .andExpect(jsonPath("$.data.location").value("Shanghai"));
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateActivityInfo_NoUpdate() throws Exception {
        ActivityInfo activityInfo = activityInfoRepository.findAll().get(0);

        ActivityInfoUpdateRequest request = ActivityInfoUpdateRequest.builder()
            .title(null)
            .location(null)
            .build();

        mockMvc.perform(put("/api/activities/{id}", activityInfo.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("没有更新"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateActivityInfo_CompanyNotFound() throws Exception {
        ActivityInfo activityInfo = activityInfoRepository.findAll().get(0);

        ActivityInfoUpdateRequest request = ActivityInfoUpdateRequest.builder()
            .companyId(9999L)
            .title("Updated Activity")
            .location("Shanghai")
            .build();

        mockMvc.perform(put("/api/activities/{id}", activityInfo.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("企业不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateActivityInfo_ActivityNotFound() throws Exception {
        ActivityInfoUpdateRequest request = ActivityInfoUpdateRequest.builder()
            .title("Updated Activity")
            .build();

        mockMvc.perform(put("/api/activities/{id}", 9999)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("活动信息不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetActivityInfo_Success() throws Exception {
        ActivityInfo activityInfo = activityInfoRepository.findAll().get(0);

        mockMvc.perform(get("/api/activities/{id}", activityInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.id").value(activityInfo.getId()))
            .andExpect(jsonPath("$.data.title").value(activityInfo.getTitle()))
            .andExpect(jsonPath("$.data.location").value(activityInfo.getLocation()))
            .andExpect(jsonPath("$.data.extra").value(activityInfo.getExtra()));
    }

    @Test
    void testGetActivityInfo_NotFound() throws Exception {
        mockMvc.perform(get("/api/activities/{id}", 9999))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(404))
            .andExpect(jsonPath("$.message").value("活动信息不存在"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllActivityInfos() throws Exception {
        mockMvc.perform(get("/api/activities")
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
    void testSearchActivityInfos() throws Exception {
        mockMvc.perform(get("/api/activities/search")
            .param("keyword", "1")
            .param("page", "1")
            .param("page_size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content", hasSize(2)))
            .andExpect(jsonPath("$.data.content[0].title").value("Activity 1"))
            .andExpect(jsonPath("$.data.content[1].company.name").value("Company 1"))
            .andExpect(jsonPath("$.data.total").value(2))
            .andExpect(jsonPath("$.data.count").value(2))
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.page_size").value(5))
            .andExpect(jsonPath("$.data.total_pages").value(1));
    }
}
