package com.example.backend.controller;

import com.example.backend.models.response.HoSoAuditLogResponse;
import com.example.backend.service.HoSoAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ho-so/audit-log")
@RequiredArgsConstructor
public class HoSoAuditLogController {

    private final HoSoAuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<Page<HoSoAuditLogResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAllAudits(pageable));
    }
}
