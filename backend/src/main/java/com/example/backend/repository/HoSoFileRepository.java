package com.example.backend.repository;

import com.example.backend.entity.HoSoFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoSoFileRepository extends JpaRepository<HoSoFile, Long> {

    /** Lấy tất cả file chưa xóa của 1 hồ sơ — dùng để check giới hạn 10 file */
    List<HoSoFile> findByHoSoIdAndHoSoTypeAndIsDeletedFalse(Long hoSoId, String hoSoType);

    /** Đếm số file active để validate max 10 file/hồ sơ */
    long countByHoSoIdAndHoSoTypeAndIsDeletedFalse(Long hoSoId, String hoSoType);
}
