package com.example.backend.repository;

import com.example.backend.entity.HoSoAnNinhMang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoSoAnNinhMangRepository extends JpaRepository<HoSoAnNinhMang, Long>,
        JpaSpecificationExecutor<HoSoAnNinhMang> {

    Optional<HoSoAnNinhMang> findByIdAndIsDeletedFalse(Long id);

    long countByMaHoSoStartingWith(String prefix);

    boolean existsByMaHoSo(String maHoSo);
}
