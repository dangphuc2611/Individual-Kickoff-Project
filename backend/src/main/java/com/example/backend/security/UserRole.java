package com.example.backend.security;

/**
 * Các role được phép trong hệ thống MOD-04.
 * Xác định quyền hạn của từng loại tài khoản.
 */
public enum UserRole {
    /** Cán bộ chuyên trách — CRUD + Import/Export trong đơn vị mình */
    CBCT,
    /** Trưởng phòng — Xem + Tìm kiếm + Export + Xem access log */
    TRUONG_PHONG,
    /** Thủ trưởng — Xem chi tiết + Xem tổng hợp toàn ngành (read-only) */
    THU_TRUONG
}
