package com.example.backend.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request body cho tạo mới / cập nhật Hồ sơ an ninh mạng (UC C04 / C05).
 */
@Data
public class HoSoAnNinhMangRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255)
    private String tieuDe;

    /** PHISHING / MALWARE / INTRUSION / KHAC */
    @NotBlank(message = "Loại tấn công không được để trống")
    private String loaiTanCong;

    /** THUONG / MAT / TOI_MAT */
    @NotBlank(message = "Mức độ mật không được để trống")
    private String mucDoMat;

    @NotBlank(message = "Hệ thống bị ảnh hưởng không được để trống")
    private String heThongBiAnhHuong;

    private String mucDoThietHai;

    @NotNull(message = "Ngày phát hiện không được để trống")
    @PastOrPresent(message = "Ngày phát hiện không được là ngày trong tương lai")
    private LocalDate ngayPhatHien;

    @NotBlank(message = "Mô tả chi tiết không được để trống")
    private String moTaChiTiet;

    /** DANG_XU_LY / DA_XU_LY / TAM_DUNG */
    private String trangThai;

    @NotNull(message = "Đơn vị quản lý không được để trống")
    private Long donViId;
}
