package com.example.backend.repository;

import com.example.backend.entity.HoSoDieuTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoSoDieuTraRepository extends JpaRepository<HoSoDieuTra, Long>,
        JpaSpecificationExecutor<HoSoDieuTra> {

    /** Tìm theo ID nhưng chỉ lấy record chưa bị xóa mềm */
    Optional<HoSoDieuTra> findByIdAndIsDeletedFalse(Long id);

    /** Đếm số hồ sơ cùng năm để sinh số thứ tự (dùng trong MaHoSoGenerator) */
    long countByMaHoSoStartingWith(String prefix);

    boolean existsByMaHoSo(String maHoSo);
}
