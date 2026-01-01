package cn.edu.zju.cs.jobmate.services;

import cn.edu.zju.cs.jobmate.dto.common.PageRequest;
import cn.edu.zju.cs.jobmate.dto.subscription.ActivitySubscriptionCreateRequest;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.ActivitySubscription;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.ActivitySubscriptionRepository;
import cn.edu.zju.cs.jobmate.services.impl.ActivitySubscriptionServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(ActivitySubscriptionServiceImpl.class)
class ActivitySubscriptionServiceTest {

    @MockitoBean
    private ActivitySubscriptionRepository activitySubscriptionRepository;

    @MockitoBean
    private ActivityInfoService activityInfoService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ActivitySubscriptionService activitySubscriptionService;

    @Test
    @SuppressWarnings("null")
    void testCreateActivitySubscription_Success() {
        long userId = 1;
        long activityInfoId = 2;
        User user = new User("testuser", "pwd", null);
        ActivityInfo activityInfo = new ActivityInfo(
            "Test Activity",
            LocalDateTime.of(2025, 1, 1, 10, 0, 0),
            "https://activity.com",
            "Hangzhou",
            "Extra"
        );

        ActivitySubscriptionCreateRequest dto = ActivitySubscriptionCreateRequest.builder()
            .userId(userId)
            .activityInfoId(activityInfoId)
            .build();

        when(userService.getById(userId)).thenReturn(user);
        when(activityInfoService.getById(activityInfoId)).thenReturn(activityInfo);

        ActivitySubscription subscription = new ActivitySubscription();
        subscription.setUser(user);
        subscription.setInfo(activityInfo);

        when(activitySubscriptionRepository.save(any(ActivitySubscription.class)))
            .thenReturn(subscription);

        ActivitySubscription result = activitySubscriptionService.create(dto);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(activityInfo, result.getInfo());
        verify(userService).getById(userId);
        verify(activityInfoService).getById(activityInfoId);
        verify(activitySubscriptionRepository).save(any(ActivitySubscription.class));
    }

    @Test
    void testCreateActivitySubscription_UserNotFound() {
        long userId = 1;
        long activityInfoId = 2;
        ActivitySubscriptionCreateRequest dto = ActivitySubscriptionCreateRequest.builder()
            .userId(userId)
            .activityInfoId(activityInfoId)
            .build();

        when(userService.getById(userId))
            .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> activitySubscriptionService.create(dto));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testCreateActivitySubscription_ActivityInfoNotFound() {
        long userId = 1;
        long activityInfoId = 2;
        ActivitySubscriptionCreateRequest dto = ActivitySubscriptionCreateRequest.builder()
            .userId(userId)
            .activityInfoId(activityInfoId)
            .build();

        when(userService.getById(userId))
            .thenReturn(new User("testuser", "pwd", null));
        when(activityInfoService.getById(activityInfoId))
            .thenThrow(new BusinessException(ErrorCode.ACTIVITY_INFO_NOT_FOUND));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> activitySubscriptionService.create(dto));
        assertEquals(ErrorCode.ACTIVITY_INFO_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testDeleteById_Success() {
        doNothing().when(activitySubscriptionRepository).deleteById(1L);
        activitySubscriptionService.deleteById(1L);
        verify(activitySubscriptionRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_MissingParameter() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> activitySubscriptionService.deleteById(null));
        assertEquals(ErrorCode.MISSING_PARAMETER, ex.getErrorCode());
    }

    @Test
    @SuppressWarnings("null")
    void testGetAllByUser_Success() {
        long userId = 1;
        PageRequest pageRequest = PageRequest.builder()
            .page(1)
            .pageSize(2)
            .build();

        ActivitySubscription sub1 = new ActivitySubscription();
        ActivitySubscription sub2 = new ActivitySubscription();
        List<ActivitySubscription> list = List.of(sub1, sub2);
        Page<ActivitySubscription> page = new PageImpl<>(list);

        when(activitySubscriptionRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(page);

        Page<ActivitySubscription> result = activitySubscriptionService.getAllByUser(userId, pageRequest);

        assertEquals(2, result.getContent().size());
        verify(activitySubscriptionRepository).findByUserId(eq(userId), any(Pageable.class));
    }
}
