package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HoSoAccessLogResponse {
    private Long id;
    private Long hoSoId;
    private String hoSoType;
    private Long userId;
    private String userName;
    /** VIEW / EXPORT / PRINT */
    private String action;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime accessedAt;
}
