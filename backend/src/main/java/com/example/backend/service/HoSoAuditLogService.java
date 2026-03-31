package com.example.backend.service;

import com.example.backend.models.response.HoSoAuditLogResponse;

import java.util.List;

public interface HoSoAuditLogService {
    /** Ghi 1 entry audit log cho 1 field thay đổi */
    void logAudit(Long recordId, String hoSoType, String action,
                  String fieldName, String oldValue, String newValue);

    /** Ghi nhiều field thay đổi cùng lúc (dùng trong update) */
    void logAuditBatch(Long recordId, String hoSoType, String action,
                       java.util.Map<String, String[]> changes);

    /** Lấy lịch sử chỉnh sửa của 1 hồ sơ — endpoint /:id/audit */
    List<HoSoAuditLogResponse> getAuditsByRecord(Long recordId, String hoSoType);
}
