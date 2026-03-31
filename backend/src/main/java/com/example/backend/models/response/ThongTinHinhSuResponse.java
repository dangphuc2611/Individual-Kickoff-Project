package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ThongTinHinhSuResponse {
    private Long id;
    private String maThongTin;
    private String tieuDe;
    private String loaiToiDanh;
    private String mucDoMat;
    private String doiTuongLienQuan;
    private String donViLienQuan;
    private LocalDate ngayXayRa;
    private String diaDiem;
    private String moTaDienBien;
    private String ketQuaXuLy;
    private Long donViId;
    private String tenDonVi;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long updatedById;
    private String updatedByName;
    private LocalDateTime updatedAt;
}
