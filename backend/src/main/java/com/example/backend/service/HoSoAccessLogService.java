package com.example.backend.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import com.example.backend.models.response.HoSoAccessLogResponse;

public interface HoSoAccessLogService {
    /** Ghi access log — gọi ở mọi GET (list + detail + export).
     *  userId phải được truyền vào từ caller (Controller/main thread) trước khi vào @Async để
     *  tránh securityContext bị mất trong worker thread pool.
     */
    void logAccess(Long userId, Long hoSoId, String hoSoType, String action, HttpServletRequest request);

    /** Log của 1 hồ sơ cụ thể — endpoint /:id/access-log */
    List<HoSoAccessLogResponse> getLogsByHoSo(Long hoSoId, String hoSoType);

    /** Tổng hợp log toàn module — endpoint /api/ho-so/access-log */
    org.springframework.data.domain.Page<HoSoAccessLogResponse> getAllLogs(
            org.springframework.data.domain.Pageable pageable);
}
