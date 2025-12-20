package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.JobSubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.JobSubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.JobSubscriptionRepository;
import cn.edu.zju.cs.jobmate.services.impl.JobSubscriptionServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(JobSubscriptionServiceImpl.class)
class JobSubscriptionServiceTest {

    @MockitoBean
    private JobSubscriptionRepository jobSubscriptionRepository;

    @MockitoBean
    private JobInfoService jobInfoService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private JobSubscriptionService jobSubscriptionService;

    @Test
    @SuppressWarnings("null")
    void testCreateJobSubscription_Success() {
        int userId = 1;
        int jobInfoId = 2;
        User user = new User("testuser", "pwd", null);
        JobInfo jobInfo = new JobInfo(
            null,
            "Java",
            null,
            null,
            null
        );

        JobSubscriptionCreateRequest dto = JobSubscriptionCreateRequest.builder()
            .userId(userId)
            .jobInfoId(jobInfoId)
            .build();

        when(userService.getById(userId)).thenReturn(user);
        when(jobInfoService.getById(jobInfoId)).thenReturn(jobInfo);

        JobSubscription subscription = new JobSubscription();
        subscription.setUser(user);
        subscription.setInfo(jobInfo);

        when(jobSubscriptionRepository.save(any(JobSubscription.class)))
            .thenReturn(subscription);

        JobSubscription result = jobSubscriptionService.create(dto);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(jobInfo, result.getInfo());
        verify(userService).getById(userId);
        verify(jobInfoService).getById(jobInfoId);
        verify(jobSubscriptionRepository).save(any(JobSubscription.class));
    }

    @Test
    void testCreateJobSubscription_UserNotFound() {
        int userId = 1;
        int jobInfoId = 2;
        JobSubscriptionCreateRequest dto = JobSubscriptionCreateRequest.builder()
            .userId(userId)
            .jobInfoId(jobInfoId)
            .build();

        when(userService.getById(userId))
            .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobSubscriptionService.create(dto));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testCreateJobSubscription_JobInfoNotFound() {
        int userId = 1;
        int jobInfoId = 2;
        JobSubscriptionCreateRequest dto = JobSubscriptionCreateRequest.builder()
            .userId(userId)
            .jobInfoId(jobInfoId)
            .build();

        when(userService.getById(userId))
            .thenReturn(new User("testuser", "pwd", null));
        when(jobInfoService.getById(jobInfoId))
            .thenThrow(new BusinessException(ErrorCode.JOB_INFO_NOT_FOUND));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobSubscriptionService.create(dto));
        assertEquals(ErrorCode.JOB_INFO_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testDeleteById_Success() {
        doNothing().when(jobSubscriptionRepository).deleteById(1);
        jobSubscriptionService.deleteById(1);
        verify(jobSubscriptionRepository).deleteById(1);
    }

    @Test
    void testDeleteById_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> jobSubscriptionService.deleteById(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllByUser_Success() {
        int userId = 1;
        PageRequest pageRequest = PageRequest.builder()
            .page(1)
            .pageSize(2)
            .build();

        JobSubscription sub1 = new JobSubscription();
        JobSubscription sub2 = new JobSubscription();
        List<JobSubscription> list = List.of(sub1, sub2);
        Page<JobSubscription> page = new PageImpl<>(list);

        when(jobSubscriptionRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        Page<JobSubscription> result = jobSubscriptionService.getAllByUser(userId, pageRequest);

        assertEquals(2, result.getContent().size());
        verify(jobSubscriptionRepository).findByUserId(eq(userId), any(Pageable.class));
    }
}
