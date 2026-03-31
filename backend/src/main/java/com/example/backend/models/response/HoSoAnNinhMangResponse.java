package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class HoSoAnNinhMangResponse {
    private Long id;
    private String maHoSo;
    private String tieuDe;
    private String loaiTanCong;
    private String mucDoMat;
    private String heThongBiAnhHuong;
    private String mucDoThietHai;
    private LocalDate ngayPhatHien;
    private String moTaChiTiet;
    private String trangThai;
    private Long donViId;
    private String tenDonVi;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime updatedAt;
}
