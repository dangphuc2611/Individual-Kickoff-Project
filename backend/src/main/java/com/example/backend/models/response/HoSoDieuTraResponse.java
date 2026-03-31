package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class HoSoDieuTraResponse {
    private Long id;
    private String maHoSo;
    private String tieuDe;
    private String phanLoai;
    private String mucDoMat;
    private String doiTuongHoTen;
    private String donViDoiTuong;
    private LocalDate ngayMoHoSo;
    private Long cbctPhuTrachId;
    private String cbctPhuTrachName;
    private String noiDung;
    private String trangThai;
    private String ghiChu;
    private Long donViId;
    private String tenDonVi;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime updatedAt;
}
