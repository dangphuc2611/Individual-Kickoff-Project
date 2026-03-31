package com.example.backend.repository;

import com.example.backend.entity.HoSoAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoSoAuditLogRepository extends JpaRepository<HoSoAuditLog, Long> {

    /** Lịch sử chỉnh sửa của 1 hồ sơ cụ thể (endpoint /:id/audit) */
    List<HoSoAuditLog> findByRecordIdAndHoSoTypeOrderByChangedAtDesc(Long recordId, String hoSoType);
}
