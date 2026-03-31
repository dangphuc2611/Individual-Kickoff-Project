package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HoSoAuditLogResponse {
    private Long id;
    private Long recordId;
    private String hoSoType;
    private Long changedById;
    private String changedByName;
    private LocalDateTime changedAt;
    /** CREATE / UPDATE / DELETE */
    private String action;
    private String fieldName;
    private String oldValue;
    private String newValue;
}
