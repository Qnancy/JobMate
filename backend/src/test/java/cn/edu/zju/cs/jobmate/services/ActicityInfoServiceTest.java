package cn.edu.zju.cs.jobmate.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import cn.edu.zju.cs.jobmate.dto.activity.*;
import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.services.impl.ActivityInfoServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(ActivityInfoServiceImpl.class)
public class ActicityInfoServiceTest {

    @MockitoBean
    private ActivityInfoRepository activityInfoRepository;

    @MockitoBean
    private CompanyService companyService;

    @Autowired
    private ActivityInfoService activityInfoService;

    @Test
    @SuppressWarnings("null")
    public void testCreateActivityInfo_Success() {
        long companyId = 1;
        Company company = new Company("Test Company", CompanyType.STATE);

        String title = "Test Title";
        LocalDateTime time = LocalDateTime.now();
        String link = "http://testlink.com";
        String location = "Test Location";
        String extra = "Test Extra Info";
        ActivityInfoCreateRequest dto = ActivityInfoCreateRequest.builder()
            .companyId(companyId)
            .title(title)
            .time(time)
            .link(link)
            .location(location)
            .extra(extra)
            .build();
        
        ActivityInfo activityInfo = dto.toModel();
        activityInfo.setCompany(company);

        when(companyService.getById(companyId)).thenReturn(company);
        when(activityInfoRepository.save(any(ActivityInfo.class))).thenReturn(activityInfo);

        ActivityInfo result = activityInfoService.create(dto);

        assertNotNull(result);
        assertEquals(company, result.getCompany());
        assertEquals(title, result.getTitle());
        assertEquals(time, result.getTime());
        assertEquals(link, result.getLink());
        assertEquals(location, result.getLocation());
        assertEquals(extra, result.getExtra());
        verify(activityInfoRepository).save(any(ActivityInfo.class));
    }

    @Test
    void testCreateActivityInfo_CompanyNotFound() {
        long companyId = 2;
        ActivityInfoCreateRequest dto = ActivityInfoCreateRequest.builder()
                .companyId(companyId)
                .title("Title")
                .time(LocalDateTime.now())
                .build();

        when(companyService.getById(companyId))
            .thenThrow(new BusinessException(ErrorCode.COMPANY_NOT_FOUND));
        
        BusinessException ex = assertThrows(BusinessException.class,
            () -> activityInfoService.create(dto));
        assertEquals(ErrorCode.COMPANY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testDeleteActivityInfo_Success() {
        doNothing().when(activityInfoRepository).deleteById(1L);
        activityInfoService.delete(1L);
        verify(activityInfoRepository).deleteById(1L);
    }

    @Test
    void testDeleteActivityInfo_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> activityInfoService.delete(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateActivityInfo_Success() {
        long activityId = 1;
        long companyId = 1;
        Company company = new Company("New Company", CompanyType.PRIVATE);
        ActivityInfo activityInfo = new ActivityInfo(
            "Old Title",
            LocalDateTime.now(),
            "http://oldlink.com",
            "Old Location",
            "Old Extra Info"
        );
        activityInfo.setCompany(new Company("Old Company", CompanyType.STATE));

        ActivityInfoUpdateRequest dto = ActivityInfoUpdateRequest.builder()
            .companyId(companyId)
            .title("New Title")
            .build();

        when(activityInfoRepository.findById(activityId)).thenReturn(Optional.of(activityInfo));
        when(companyService.getById(companyId)).thenReturn(company);
        when(activityInfoRepository.save(any(ActivityInfo.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        ActivityInfo result = activityInfoService.update(activityId, dto);
        assertEquals("New Title", result.getTitle());
        assertEquals(company, result.getCompany());
    }

    @Test
    void testUpdateActivityInfo_NoUpdates() {
        long activityId = 1;
        ActivityInfo activityInfo = new ActivityInfo(
            "Title",
            LocalDateTime.now(),
            "http://link.com",
            "Location",
            "Extra Info"
        );

        ActivityInfoUpdateRequest dto = ActivityInfoUpdateRequest.builder().build();

        when(activityInfoRepository.findById(activityId)).thenReturn(Optional.of(activityInfo));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> activityInfoService.update(activityId, dto));
        assertEquals(ErrorCode.NO_UPDATES, ex.getErrorCode());
    }

    @Test
    void testUpdateActivityInfo_CompanyNotFound() {
        long activityId = 1;
        long companyId = 2;
        ActivityInfo activityInfo = new ActivityInfo(
            "Title",
            LocalDateTime.now(),
            "http://link.com",
            "Location",
            "Extra Info"
        );

        ActivityInfoUpdateRequest dto = ActivityInfoUpdateRequest.builder()
            .companyId(companyId)
            .build();

        when(activityInfoRepository.findById(activityId)).thenReturn(Optional.of(activityInfo));
        when(companyService.getById(companyId))
            .thenThrow(new BusinessException(ErrorCode.COMPANY_NOT_FOUND));
        
        BusinessException ex = assertThrows(BusinessException.class,
            () -> activityInfoService.update(activityId, dto));
        assertEquals(ErrorCode.COMPANY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testGetById_Success() {
        long activityId = 1;
        ActivityInfo activityInfo = new ActivityInfo(
            "Title",
            LocalDateTime.now(),
            "http://link.com",
            "Location",
            "Extra Info"
        );
        when(activityInfoRepository.findById(activityId)).thenReturn(Optional.of(activityInfo));

        ActivityInfo result = activityInfoService.getById(activityId);
        assertEquals(activityInfo, result);
    }

    @Test
    void testGetById_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> activityInfoService.getById(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    void testGetById_NotFound() {
        long activityId = 1;
        when(activityInfoRepository.findById(activityId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
            () -> activityInfoService.getById(activityId));
        assertEquals(ErrorCode.ACTIVITY_INFO_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAll() {
        List<ActivityInfo> activityInfos = List.of(
            new ActivityInfo(
                "Title1",
                LocalDateTime.now(),
                "http://link1.com",
                "Location1",
                "Extra1"
            ),
            new ActivityInfo(
                "Title2",
                LocalDateTime.now(),
                "http://link2.com",
                "Location2",
                "Extra2"
            )
        );
        Page<ActivityInfo> page = new PageImpl<>(
            activityInfos,
            org.springframework.data.domain.PageRequest.of(0, 2),
            2
        );
        PageRequest dto = cn.edu.zju.cs.jobmate.dto.common.PageRequest.builder()
            .page(1)
            .pageSize(2)
            .build();
        when(activityInfoRepository.findAll(any(org.springframework.data.domain.PageRequest.class))).thenReturn(page);

        Page<ActivityInfo> result = activityInfoService.getAll(dto);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    @SuppressWarnings({"null", "unchecked"})
    void testQuery() {
        String keyword = "keyword";
        int page = 1;
        int pageSize = 2;

        ActivityInfoQueryRequest dto = ActivityInfoQueryRequest.builder()
            .keyword(keyword)
            .page(page)
            .pageSize(pageSize)
            .build();

        List<ActivityInfo> activityInfos = List.of(
            new ActivityInfo(
                "Title1",
                LocalDateTime.now(),
                "http://link1.com",
                "Location1",
                "Extra1"
            ),
            new ActivityInfo(
                "Title2",
                LocalDateTime.now(),
                "http://link2.com",
                "Location2",
                "Extra2"
            )
        );
        Page<ActivityInfo> pageResult = new PageImpl<>(
            activityInfos,
            org.springframework.data.domain.PageRequest.of(page - 1, pageSize),
            2
        );

        when(activityInfoRepository.findAll(any(Specification.class), any(org.springframework.data.domain.PageRequest.class)))
            .thenReturn(pageResult);

        org.springframework.data.domain.Page<ActivityInfo> result = activityInfoService.query(dto);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(activityInfoRepository).findAll(any(Specification.class), any(org.springframework.data.domain.PageRequest.class));
    }
}
