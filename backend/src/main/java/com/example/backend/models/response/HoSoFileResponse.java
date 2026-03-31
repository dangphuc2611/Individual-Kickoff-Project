package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HoSoFileResponse {
    private Long id;
    private Long hoSoId;
    private String hoSoType;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private Long uploadedById;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
}
