package com.example.backend.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request body cho tạo mới / cập nhật Hồ sơ điều tra cơ bản (UC A04 / A05).
 */
@Data
public class HoSoDieuTraRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String tieuDe;

    /** DIEU_TRA_CO_BAN / THEO_DOI / DAC_BIET */
    @NotBlank(message = "Phân loại không được để trống")
    private String phanLoai;

    /** THUONG / MAT / TOI_MAT */
    @NotBlank(message = "Mức độ mật không được để trống")
    private String mucDoMat;

    @NotBlank(message = "Họ tên đối tượng không được để trống")
    @Size(max = 255)
    private String doiTuongHoTen;

    @NotBlank(message = "Đơn vị đối tượng không được để trống")
    @Size(max = 255)
    private String donViDoiTuong;

    /** Không được > hôm nay */
    @NotNull(message = "Ngày mở hồ sơ không được để trống")
    @PastOrPresent(message = "Ngày mở hồ sơ không được là ngày trong tương lai")
    private LocalDate ngayMoHoSo;

    /**
     * ID cán bộ phụ trách.
     * Nếu null, Service sẽ tự gán = currentUser.id (theo đặc tả)
     */
    private Long cbctPhuTrachId;

    @NotBlank(message = "Nội dung không được để trống")
    private String noiDung;

    /** DANG_THEO_DOI / TAM_DUNG / KET_THUC — nếu null thì mặc định DANG_THEO_DOI */
    private String trangThai;

    @Size(max = 1000, message = "Ghi chú tối đa 1000 ký tự")
    private String ghiChu;

    @NotNull(message = "Đơn vị quản lý không được để trống")
    private Long donViId;
}
