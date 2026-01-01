package cn.edu.zju.cs.jobmate.controllers;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.dto.activity.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.services.ActivityInfoService;
import cn.edu.zju.cs.jobmate.testing.ControllerTestStartUp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ActivityInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = MonitorProperties.class)
class ActivityInfoControllerTest extends ControllerTestStartUp {

    @MockitoBean
    private ActivityInfoService activityInfoService;

    @Test
    @SuppressWarnings("null")
    void testCreateActivityInfo() throws Exception {
        ActivityInfoCreateRequest request = ActivityInfoCreateRequest.builder()
            .companyId(1L)
            .title("Test Activity")
            .time(LocalDateTime.now())
            .link("https://activity.example.com")
            .location("Hangzhou")
            .extra("Extra Info")
            .build();

        Company company = new Company("Test Company", CompanyType.STATE);
        ActivityInfo activityInfo = request.toModel();
        activityInfo.setCompany(company);

        when(activityInfoService.create(any(ActivityInfoCreateRequest.class)))
            .thenReturn(activityInfo);

        mockMvc.perform(post("/api/activities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("创建成功"))
            .andExpect(jsonPath("$.data.title").value("Test Activity"))
            .andExpect(jsonPath("$.data.location").value("Hangzhou"))
            .andExpect(jsonPath("$.data.extra").value("Extra Info"));
    }

    @Test
    void testDeleteActivityInfo() throws Exception {
        doNothing().when(activityInfoService).delete(1L);

        mockMvc.perform(delete("/api/activities/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("删除成功"))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateActivityInfo() throws Exception {
        ActivityInfoUpdateRequest request = ActivityInfoUpdateRequest.builder()
            .title("Updated Activity")
            .location("Shanghai")
            .build();

        ActivityInfo activityInfo = new ActivityInfo(
            "Updated Activity",
            LocalDateTime.now(),
            "https://activity.example.com",
            "Shanghai",
            "Updated Extra"
        );

        when(activityInfoService.update(eq(1L), any(ActivityInfoUpdateRequest.class)))
            .thenReturn(activityInfo);

        mockMvc.perform(put("/api/activities/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"))
            .andExpect(jsonPath("$.data.title").value("Updated Activity"))
            .andExpect(jsonPath("$.data.location").value("Shanghai"));
    }

    @Test
    void testGetActivityInfo() throws Exception {
        ActivityInfo activityInfo = new ActivityInfo(
            "Test Activity",
            LocalDateTime.now(),
            "https://activity.example.com",
            "Hangzhou",
            "Extra Info"
        );

        when(activityInfoService.getById(1L)).thenReturn(activityInfo);

        mockMvc.perform(get("/api/activities/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.title").value("Test Activity"))
            .andExpect(jsonPath("$.data.location").value("Hangzhou"))
            .andExpect(jsonPath("$.data.extra").value("Extra Info"));
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllActivityInfos() throws Exception {
        ActivityInfo activity1 = new ActivityInfo(
            "Activity 1",
            LocalDateTime.now(),
            "https://a1.com",
            "City 1",
            "Extra 1"
        );
        ActivityInfo activity2 = new ActivityInfo(
            "Activity 2",
            LocalDateTime.now(),
            "https://a2.com",
            "City 2",
            "Extra 2"
        );

        Page<ActivityInfo> page = new PageImpl<>(List.of(activity1, activity2));

        when(activityInfoService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/activities")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content[0].title").value("Activity 1"))
            .andExpect(jsonPath("$.data.content[1].title").value("Activity 2"));
    }

    @Test
    @SuppressWarnings("null")
    void testSearchActivityInfos() throws Exception {
        ActivityInfo activityInfo = new ActivityInfo(
            "Search Activity",
            LocalDateTime.now(),
            "https://search.com",
            "Search City",
            "Search Extra"
        );

        Page<ActivityInfo> page = new PageImpl<>(List.of(activityInfo));

        when(activityInfoService.query(any(ActivityInfoQueryRequest.class)))
            .thenReturn(page);

        mockMvc.perform(get("/api/activities/search")
            .param("keyword", "Search")
            .param("page", "1")
            .param("page_size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("查询成功"))
            .andExpect(jsonPath("$.data.content[0].title").value("Search Activity"))
            .andExpect(jsonPath("$.data.content[0].location").value("Search City"))
            .andExpect(jsonPath("$.data.content[0].extra").value("Search Extra"));
    }
}
