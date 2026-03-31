package com.example.backend.controller;

import com.example.backend.models.response.HoSoAccessLogResponse;
import com.example.backend.service.HoSoAccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ho-so")
@RequiredArgsConstructor
public class HoSoAccessLogController {

    private final HoSoAccessLogService accessLogService;

    /**
     * Endpoint tổng hợp toàn bộ access log của hệ thống
     * GET /api/ho-so/access-log
     */
    @GetMapping("/access-log")
    public ResponseEntity<Page<HoSoAccessLogResponse>> getAllLogs(Pageable pageable) {
        return ResponseEntity.ok(accessLogService.getAllLogs(pageable));
    }
}
