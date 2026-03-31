package com.example.backend.models.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * Query params chung cho tìm kiếm hồ sơ.
 * Dùng cho cả 3 nhóm A / B / C với các field tương ứng.
 */
@Data
public class HoSoFilterParams {

    /** Tìm kiếm fulltext: mã HS, tiêu đề, tên đối tượng */
    private String search;

    /** THUONG / MAT / TOI_MAT */
    private String mucDoMat;

    /** Trạng thái: DANG_THEO_DOI / TAM_DUNG / KET_THUC (điều tra)
     *             DANG_XU_LY / DA_XU_LY / CHUYEN_CO_QUAN_KHAC (hình sự)
     *             DANG_XU_LY / DA_XU_LY / TAM_DUNG (an ninh mạng) */
    private String trangThai;

    /** Lọc theo 1 hoặc nhiều đơn vị (don_vi_ids[]) */
    private List<Long> donViIds;

    /** Loại tội danh (nhóm B) hoặc loại tấn công (nhóm C) */
    private String loai;

    /** Ngày bắt đầu khoảng thời gian (ngày mở / ngày xảy ra / ngày phát hiện) */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    /** Ngày kết thúc khoảng thời gian */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;
}
