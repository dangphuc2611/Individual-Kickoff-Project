package com.example.backend.repository;

import com.example.backend.entity.ThongTinHinhSu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThongTinHinhSuRepository extends JpaRepository<ThongTinHinhSu, Long>,
        JpaSpecificationExecutor<ThongTinHinhSu> {

    Optional<ThongTinHinhSu> findByIdAndIsDeletedFalse(Long id);

    long countByMaThongTinStartingWith(String prefix);

    boolean existsByMaThongTin(String maThongTin);
}
