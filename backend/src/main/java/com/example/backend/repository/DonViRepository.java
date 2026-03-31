package com.example.backend.repository;

import com.example.backend.entity.DonVi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonViRepository extends JpaRepository<DonVi, Long> {
    List<DonVi> findByIsActiveTrue();
    boolean existsByMaDonVi(String maDonVi);
}
