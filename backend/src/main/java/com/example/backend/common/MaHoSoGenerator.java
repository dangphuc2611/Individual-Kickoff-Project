package com.example.backend.common;

import com.example.backend.repository.HoSoAnNinhMangRepository;
import com.example.backend.repository.HoSoDieuTraRepository;
import com.example.backend.repository.ThongTinHinhSuRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * Sinh mã hồ sơ duy nhất theo format đặc tả REQ.
 *   DTRA-YYYY-XXXXX  — Hồ sơ điều tra cơ bản
 *   HSTU-YYYY-XXXXX  — Thông tin hình sự
 *   ANMG-YYYY-XXXXX  — Hồ sơ an ninh mạng
 *
 * Race-condition safe: dùng REQUIRES_NEW + Pessimistic Lock trên bảng tương ứng
 * để đảm bảo không 2 transaction nào đọc cùng 1 max count.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaHoSoGenerator {

    private final HoSoDieuTraRepository hoSoDieuTraRepository;
    private final ThongTinHinhSuRepository thongTinHinhSuRepository;
    private final HoSoAnNinhMangRepository hoSoAnNinhMangRepository;
    private final EntityManager entityManager;

    /**
     * DTRA-YYYY-XXXXX — gọi trong transaction createHoSo, tự tạo sub-tx riêng.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateDieuTra() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "DTRA-" + year + "-";

        // Pessimistic write lock: block concurrent count reads
        entityManager.createNativeQuery(
                "SELECT pg_advisory_xact_lock(1001)")
                .getSingleResult();

        long count = hoSoDieuTraRepository.countByMaHoSoStartingWith(prefix);
        String candidate;
        do {
            count++;
            candidate = prefix + String.format("%05d", count);
        } while (hoSoDieuTraRepository.existsByMaHoSo(candidate));

        log.debug("Generated ma_ho_so DTRA: {}", candidate);
        return candidate;
    }

    /**
     * HSTU-YYYY-XXXXX — thông tin hình sự.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateHinhSu() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "HSTU-" + year + "-";

        entityManager.createNativeQuery(
                "SELECT pg_advisory_xact_lock(1002)")
                .getSingleResult();

        long count = thongTinHinhSuRepository.countByMaThongTinStartingWith(prefix);
        String candidate;
        do {
            count++;
            candidate = prefix + String.format("%05d", count);
        } while (thongTinHinhSuRepository.existsByMaThongTin(candidate));

        log.debug("Generated ma_thong_tin HSTU: {}", candidate);
        return candidate;
    }

    /**
     * ANMG-YYYY-XXXXX — hồ sơ an ninh mạng.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateAnNinhMang() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "ANMG-" + year + "-";

        entityManager.createNativeQuery(
                "SELECT pg_advisory_xact_lock(1003)")
                .getSingleResult();

        long count = hoSoAnNinhMangRepository.countByMaHoSoStartingWith(prefix);
        String candidate;
        do {
            count++;
            candidate = prefix + String.format("%05d", count);
        } while (hoSoAnNinhMangRepository.existsByMaHoSo(candidate));

        log.debug("Generated ma_ho_so ANMG: {}", candidate);
        return candidate;
    }
}
