package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.job.*;
import cn.edu.zju.cs.jobmate.enums.CompanyType;
import cn.edu.zju.cs.jobmate.enums.RecruitType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.Company;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.services.impl.JobInfoServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@Import(JobInfoServiceImpl.class)
class JobInfoServiceTest {

    @MockitoBean
    private JobInfoRepository jobInfoRepository;

    @MockitoBean
    private CompanyService companyService;

    @Autowired
    private JobInfoService jobInfoService;

    @Test
    @SuppressWarnings("null")
    void testCreateJobInfo_Success() {
        int companyId = 1;
        Company company = new Company("Test Company", CompanyType.STATE);

        RecruitType type = RecruitType.CAMPUS;
        String position = "Java developer";
        String link = "https://job.example.com";
        String location = "Hangzhou";
        String extra = "Five insurances and one fund";
        JobInfoCreateRequest dto = JobInfoCreateRequest.builder()
            .companyId(companyId)
            .recruitType(type)
            .position(position)
            .link(link)
            .location(location)
            .extra(extra)
            .build();

        JobInfo jobInfo = dto.toModel();
        jobInfo.setCompany(company);

        when(companyService.getById(companyId)).thenReturn(company);
        when(jobInfoRepository.save(any(JobInfo.class))).thenReturn(jobInfo);

        JobInfo result = jobInfoService.create(dto);

        assertNotNull(result);
        assertEquals(position, result.getPosition());
        assertEquals(location, result.getLocation());
        assertEquals(company, result.getCompany());
        verify(jobInfoRepository).save(any(JobInfo.class));
        verify(companyService).getById(companyId);
    }

    @Test
    void testCreateJobInfo_CompanyNotFound() {
        int companyId = 2;
        JobInfoCreateRequest dto = JobInfoCreateRequest.builder()
            .companyId(companyId)
            .position("Java")
            .build();

        when(companyService.getById(companyId))
            .thenThrow(new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobInfoService.create(dto));
        assertEquals(ErrorCode.COMPANY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testDeleteJobInfo_Success() {
        doNothing().when(jobInfoRepository).deleteById(1);
        jobInfoService.delete(1);
        verify(jobInfoRepository).deleteById(1);
    }

    @Test
    void testDeleteJobInfo_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobInfoService.delete(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    @SuppressWarnings("null")
    void testUpdateJobInfo_Success() {
        int jobId = 1;
        int companyId = 2;
        Company company = new Company("New Company", CompanyType.PRIVATE);
        JobInfo jobInfo = new JobInfo(
            RecruitType.CAMPUS,
            "Old position",
            "https://old.link",
            "Old Location",
            null
        );
        jobInfo.setCompany(new Company("Old Company", CompanyType.STATE));

        JobInfoUpdateRequest dto = JobInfoUpdateRequest.builder()
            .companyId(companyId)
            .position("New Position")
            .build();

        when(jobInfoRepository.findById(jobId)).thenReturn(Optional.of(jobInfo));
        when(companyService.getById(companyId)).thenReturn(company);
        when(jobInfoRepository.save(any(JobInfo.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        JobInfo updated = jobInfoService.update(jobId, dto);
        assertEquals("New Position", updated.getPosition());
        assertEquals(company, updated.getCompany());
    }

    @Test
    void testUpdateJobInfo_NoUpdates() {
        int jobId = 1;
        JobInfo jobInfo = new JobInfo(
            RecruitType.CAMPUS,
            "Old position",
            "https://old.link",
            "Old Location",
            null
        );
        JobInfoUpdateRequest dto = JobInfoUpdateRequest.builder().build();

        when(jobInfoRepository.findById(jobId)).thenReturn(Optional.of(jobInfo));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobInfoService.update(jobId, dto));
        assertEquals(ErrorCode.NO_UPDATES, ex.getErrorCode());
    }

    @Test
    void testUpdateJobInfo_CompanyNotFound() {
        int jobId = 1;
        int companyId = 2;
        JobInfo jobInfo = new JobInfo(
            RecruitType.CAMPUS,
            "Old position",
            "https://old.link",
            "Old Location",
            null
        );
        JobInfoUpdateRequest dto = JobInfoUpdateRequest.builder()
            .companyId(companyId)
            .build();

        when(jobInfoRepository.findById(jobId)).thenReturn(Optional.of(jobInfo));
        when(companyService.getById(companyId))
            .thenThrow(new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobInfoService.update(jobId, dto));
        assertEquals(ErrorCode.COMPANY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testGetById_Success() {
        int jobId = 1;
        JobInfo jobInfo = new JobInfo(
            RecruitType.CAMPUS,
            "Position",
            "https://link",
            "Location",
            null
        );
        when(jobInfoRepository.findById(jobId)).thenReturn(Optional.of(jobInfo));

        JobInfo result = jobInfoService.getById(jobId);
        assertEquals(jobInfo, result);
    }

    @Test
    void testGetById_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobInfoService.getById(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    void testGetById_NotFound() {
        int jobId = 1;
        when(jobInfoRepository.findById(jobId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobInfoService.getById(jobId));
        assertEquals(ErrorCode.JOB_INFO_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAll() {
        List<JobInfo> jobInfos = List.of(
            new JobInfo(
                RecruitType.CAMPUS,
                "Position1",
                "https://link1",
                "Location1",
                null
            ),
            new JobInfo(
                RecruitType.INTERN,
                "Position2",
                "https://link2",
                "Location2",
                null
            )
        );
        Page<JobInfo> page = new PageImpl<>(
            jobInfos,
            PageRequest.of(0, 2),
            2
        );
        cn.edu.zju.cs.jobmate.dto.common.PageRequest dto = cn.edu.zju.cs.jobmate.dto.common.PageRequest.builder()
            .page(1)
            .pageSize(2)
            .build();
        when(jobInfoRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<JobInfo> result = jobInfoService.getAll(dto);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    @SuppressWarnings({"null", "unchecked"})
    void testQuery() {
        String keyword = "keyword";
        RecruitType recruitType = RecruitType.CAMPUS;
        int page = 1;
        int pageSize = 2;

        JobInfoQueryRequest dto = JobInfoQueryRequest.builder()
            .keyword(keyword)
            .recruitType(recruitType)
            .page(page)
            .pageSize(pageSize)
            .build();

        List<JobInfo> jobInfos = List.of(
            new JobInfo(
                RecruitType.CAMPUS,
                "Position1",
                "https://link1",
                "Location1",
                null
            ),
            new JobInfo(
                RecruitType.CAMPUS,
                "Position2",
                "https://link2",
                "Location2",
                null
            )
        );
        Page<JobInfo> pageResult = new PageImpl<>(
            jobInfos,
            PageRequest.of(page - 1, pageSize),
            2
        );

        when(jobInfoRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(pageResult);

        Page<JobInfo> result = jobInfoService.query(dto);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        verify(jobInfoRepository).findAll(any(Specification.class), any(PageRequest.class));
    }
}
