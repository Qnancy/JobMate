package cn.edu.zju.cs.jobmate.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.zju.cs.jobmate.dto.activity.ActivityInfoResponse;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteListRequest;
import cn.edu.zju.cs.jobmate.dto.favorite.FavoriteResponse;
import cn.edu.zju.cs.jobmate.dto.job.JobInfoResponse;
import cn.edu.zju.cs.jobmate.enums.FavoriteTargetType;
import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import cn.edu.zju.cs.jobmate.models.ActivityInfo;
import cn.edu.zju.cs.jobmate.models.Favorite;
import cn.edu.zju.cs.jobmate.models.JobInfo;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.repositories.ActivityInfoRepository;
import cn.edu.zju.cs.jobmate.repositories.FavoriteRepository;
import cn.edu.zju.cs.jobmate.repositories.JobInfoRepository;
import cn.edu.zju.cs.jobmate.services.FavoriteService;
import cn.edu.zju.cs.jobmate.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Favorite service implementation.
 *
 * @see FavoriteService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final JobInfoRepository jobInfoRepository;
    private final ActivityInfoRepository activityInfoRepository;
    private final UserService userService;

    @Override
    @Transactional
    public boolean add(FavoriteTargetType targetType, Long targetId) {
        validateInput(targetType, targetId);
        User user = userService.getCurrentUser();

        // Idempotent: if the row already exists, don't create a duplicate.
        if (favoriteRepository.existsByUser_IdAndTargetTypeAndTargetId(user.getId(), targetType, targetId)) {
            return false;
        }

        // Validate the target really exists so we never store dangling favorites.
        ensureTargetExists(targetType, targetId);

        favoriteRepository.save(new Favorite(user, targetType, targetId));
        log.info("User(id={}) favorited {}(id={})", user.getId(), targetType, targetId);
        return true;
    }

    @Override
    @Transactional
    public boolean remove(FavoriteTargetType targetType, Long targetId) {
        validateInput(targetType, targetId);
        User user = userService.getCurrentUser();
        int deleted = favoriteRepository.deleteByUserAndTarget(user.getId(), targetType, targetId);
        if (deleted > 0) {
            log.info("User(id={}) unfavorited {}(id={})", user.getId(), targetType, targetId);
        }
        return deleted > 0;
    }

    @Override
    @Transactional
    public boolean toggle(FavoriteTargetType targetType, Long targetId) {
        validateInput(targetType, targetId);
        User user = userService.getCurrentUser();

        if (favoriteRepository.existsByUser_IdAndTargetTypeAndTargetId(user.getId(), targetType, targetId)) {
            favoriteRepository.deleteByUserAndTarget(user.getId(), targetType, targetId);
            log.info("User(id={}) toggled OFF {}(id={})", user.getId(), targetType, targetId);
            return false;
        }

        ensureTargetExists(targetType, targetId);
        favoriteRepository.save(new Favorite(user, targetType, targetId));
        log.info("User(id={}) toggled ON {}(id={})", user.getId(), targetType, targetId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(FavoriteTargetType targetType, Long targetId) {
        validateInput(targetType, targetId);
        User user = userService.getCurrentUser();
        return favoriteRepository.existsByUser_IdAndTargetTypeAndTargetId(user.getId(), targetType, targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> existsBatch(FavoriteTargetType targetType, Collection<Long> ids) {
        if (targetType == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        User user = userService.getCurrentUser();
        return favoriteRepository.findFavoritedTargetIds(user.getId(), targetType, ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> list(FavoriteListRequest request) {
        User user = userService.getCurrentUser();

        Page<Favorite> page = (request.getTargetType() == null)
            ? favoriteRepository.findByUser_IdOrderByCreatedAtDesc(user.getId(), request.toPageable())
            : favoriteRepository.findByUser_IdAndTargetTypeOrderByCreatedAtDesc(
                user.getId(), request.getTargetType(), request.toPageable());

        // Batch-fetch job and activity payloads to avoid N+1 queries.
        List<Favorite> favorites = page.getContent();
        Set<Long> jobIds = new HashSet<>();
        Set<Long> activityIds = new HashSet<>();
        for (Favorite f : favorites) {
            if (f.getTargetType() == FavoriteTargetType.JOB) {
                jobIds.add(f.getTargetId());
            } else if (f.getTargetType() == FavoriteTargetType.ACTIVITY) {
                activityIds.add(f.getTargetId());
            }
        }

        Map<Long, JobInfo> jobMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            jobInfoRepository.findAllById(jobIds).forEach(j -> jobMap.put(j.getId(), j));
        }
        Map<Long, ActivityInfo> activityMap = new HashMap<>();
        if (!activityIds.isEmpty()) {
            activityInfoRepository.findAllById(activityIds).forEach(a -> activityMap.put(a.getId(), a));
        }

        return page.map(f -> {
            FavoriteResponse resp = FavoriteResponse.from(f);
            if (f.getTargetType() == FavoriteTargetType.JOB) {
                JobInfo job = jobMap.get(f.getTargetId());
                if (job != null) {
                    resp.withJob(JobInfoResponse.from(job));
                }
            } else if (f.getTargetType() == FavoriteTargetType.ACTIVITY) {
                ActivityInfo activity = activityMap.get(f.getTargetId());
                if (activity != null) {
                    resp.withActivity(ActivityInfoResponse.from(activity));
                }
            }
            return resp;
        });
    }

    // --- helpers ---

    private void validateInput(FavoriteTargetType targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            throw new BusinessException(ErrorCode.MISSING_PARAMETER);
        }
        if (targetId <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER);
        }
    }

    private void ensureTargetExists(FavoriteTargetType targetType, Long targetId) {
        boolean exists = switch (targetType) {
            case JOB -> jobInfoRepository.existsById(targetId);
            case ACTIVITY -> activityInfoRepository.existsById(targetId);
        };
        if (!exists) {
            throw new BusinessException(ErrorCode.FAVORITE_TARGET_NOT_FOUND);
        }
    }
}
