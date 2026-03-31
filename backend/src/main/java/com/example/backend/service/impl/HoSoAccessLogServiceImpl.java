package com.example.backend.service.impl;

import com.example.backend.entity.HoSoAccessLog;
import com.example.backend.entity.User;
import com.example.backend.models.response.HoSoAccessLogResponse;
import com.example.backend.repository.HoSoAccessLogRepository;
import com.example.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import com.example.backend.service.HoSoAccessLogService;

/**
 * Ghi access log cho MỌI truy cập hồ sơ (GET list, GET detail, Export).
 * Dùng @Async để không block response trả về cho client.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HoSoAccessLogServiceImpl implements HoSoAccessLogService {

    private final HoSoAccessLogRepository accessLogRepository;
    private final UserRepository userRepository;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAccess(Long userId, Long hoSoId, String hoSoType, String action, HttpServletRequest request) {
        try {
            User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;

            HoSoAccessLog logEntry = HoSoAccessLog.builder()
                    .hoSoId(hoSoId)
                    .hoSoType(hoSoType)
                    .user(user)
                    .action(action)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .accessedAt(LocalDateTime.now())
                    .build();

            accessLogRepository.save(logEntry);
        } catch (Exception e) {
            // Log nhưng không để lỗi ảnh hưởng response chính
            log.error("Lỗi ghi access log hoSoId={} type={}: {}", hoSoId, hoSoType, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoSoAccessLogResponse> getLogsByHoSo(Long hoSoId, String hoSoType) {
        return accessLogRepository
                .findByHoSoIdAndHoSoTypeOrderByAccessedAtDesc(hoSoId, hoSoType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HoSoAccessLogResponse> getAllLogs(Pageable pageable) {
        return accessLogRepository.findAllByOrderByAccessedAtDesc(pageable)
                .map(this::toResponse);
    }

    private HoSoAccessLogResponse toResponse(HoSoAccessLog log) {
        return HoSoAccessLogResponse.builder()
                .id(log.getId())
                .hoSoId(log.getHoSoId())
                .hoSoType(log.getHoSoType())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .userName(log.getUser() != null ? log.getUser().getName() : null)
                .action(log.getAction())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .accessedAt(log.getAccessedAt())
                .build();
    }

    /** Lấy IP thực từ X-Forwarded-For header (khi đứng sau proxy/load balancer) */
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
