package com.example.backend.service.impl;

import com.example.backend.entity.HoSoAuditLog;
import com.example.backend.entity.User;
import com.example.backend.models.response.HoSoAuditLogResponse;
import com.example.backend.repository.HoSoAuditLogRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.SecurityUtils;
import com.example.backend.service.HoSoAuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Ghi lịch sử thay đổi dữ liệu (CREATE / UPDATE / DELETE).
 * Mỗi field thay đổi → 1 entry trong ho_so_audit_log.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HoSoAuditLogServiceImpl implements HoSoAuditLogService {

    private final HoSoAuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAudit(Long recordId, String hoSoType, String action,
                         String fieldName, String oldValue, String newValue) {
        User currentUser = getCurrentUser();
        HoSoAuditLog entry = HoSoAuditLog.builder()
                .recordId(recordId)
                .hoSoType(hoSoType)
                .changedBy(currentUser)
                .changedAt(LocalDateTime.now())
                .action(action)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        auditLogRepository.save(entry);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuditBatch(Long recordId, String hoSoType, String action,
                              Map<String, String[]> changes) {
        if (changes == null || changes.isEmpty()) return;
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        List<HoSoAuditLog> entries = changes.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().length == 2)
                // Chỉ ghi nếu có thay đổi thực sự (old != new)
                .filter(e -> !java.util.Objects.equals(e.getValue()[0], e.getValue()[1]))
                .map(e -> HoSoAuditLog.builder()
                        .recordId(recordId)
                        .hoSoType(hoSoType)
                        .changedBy(currentUser)
                        .changedAt(now)
                        .action(action)
                        .fieldName(e.getKey())
                        .oldValue(e.getValue()[0])
                        .newValue(e.getValue()[1])
                        .build())
                .toList();

        auditLogRepository.saveAll(entries);
        log.debug("Ghi {} audit log entries cho recordId={} type={}", entries.size(), recordId, hoSoType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoSoAuditLogResponse> getAuditsByRecord(Long recordId, String hoSoType) {
        return auditLogRepository
                .findByRecordIdAndHoSoTypeOrderByChangedAtDesc(recordId, hoSoType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private HoSoAuditLogResponse toResponse(HoSoAuditLog log) {
        return HoSoAuditLogResponse.builder()
                .id(log.getId())
                .recordId(log.getRecordId())
                .hoSoType(log.getHoSoType())
                .changedById(log.getChangedBy() != null ? log.getChangedBy().getId() : null)
                .changedByName(log.getChangedBy() != null ? log.getChangedBy().getName() : null)
                .changedAt(log.getChangedAt())
                .action(log.getAction())
                .fieldName(log.getFieldName())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .build();
    }

    private User getCurrentUser() {
        return userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user hiện tại trong DB"));
    }
}
