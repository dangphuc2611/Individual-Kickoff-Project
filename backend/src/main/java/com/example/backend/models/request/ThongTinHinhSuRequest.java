package com.example.backend.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request body cho tạo mới / cập nhật Thông tin hình sự - tội phạm (UC B04 / B05).
 */
@Data
public class ThongTinHinhSuRequest {

    @NotBlank(message = "Tiêu đề vụ việc không được để trống")
    @Size(max = 255)
    private String tieuDe;

    /** HINH_SU / MA_TUY / THAM_NHUNG / KHAC */
    @NotBlank(message = "Loại tội danh không được để trống")
    private String loaiToiDanh;

    /** THUONG / MAT / TOI_MAT */
    @NotBlank(message = "Mức độ mật không được để trống")
    private String mucDoMat;

    /** Nhiều người — ngăn cách dấu phẩy */
    @NotBlank(message = "Đối tượng liên quan không được để trống")
    private String doiTuongLienQuan;

    @NotBlank(message = "Đơn vị liên quan không được để trống")
    private String donViLienQuan;

    @NotNull(message = "Ngày xảy ra không được để trống")
    @PastOrPresent(message = "Ngày xảy ra không được là ngày trong tương lai")
    private LocalDate ngayXayRa;

    private String diaDiem;

    @NotBlank(message = "Mô tả diễn biến không được để trống")
    private String moTaDienBien;

    /** DANG_XU_LY / DA_XU_LY / CHUYEN_CO_QUAN_KHAC */
    private String ketQuaXuLy;

    @NotNull(message = "Đơn vị quản lý không được để trống")
    private Long donViId;
}
