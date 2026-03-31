package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DonViResponse {
    private Long id;
    private String maDonVi;
    private String tenDonVi;
    private String capDonVi;
    private Long parentId;
    private String parentName;
    private boolean isActive;
    private LocalDateTime createdAt;
}
