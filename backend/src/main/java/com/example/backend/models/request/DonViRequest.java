package com.example.backend.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DonViRequest {
    @NotBlank(message = "Mã đơn vị không được để trống")
    private String maDonVi;

    @NotBlank(message = "Tên đơn vị không được để trống")
    private String tenDonVi;

    private String capDonVi;
    private Long parentId;
    private Boolean isActive;
}
