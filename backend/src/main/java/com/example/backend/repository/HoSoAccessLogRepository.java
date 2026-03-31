package com.example.backend.repository;

import com.example.backend.entity.HoSoAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoSoAccessLogRepository extends JpaRepository<HoSoAccessLog, Long> {

    /** Log của 1 hồ sơ cụ thể (endpoint /:id/access-log) */
    List<HoSoAccessLog> findByHoSoIdAndHoSoTypeOrderByAccessedAtDesc(Long hoSoId, String hoSoType);

    /** Log theo user (Trưởng phòng xem ai đã truy cập) */
    Page<HoSoAccessLog> findByUserIdOrderByAccessedAtDesc(Long userId, Pageable pageable);

    /** Tổng hợp log (GET /api/ho-so/access-log) */
    Page<HoSoAccessLog> findAllByOrderByAccessedAtDesc(Pageable pageable);
}
