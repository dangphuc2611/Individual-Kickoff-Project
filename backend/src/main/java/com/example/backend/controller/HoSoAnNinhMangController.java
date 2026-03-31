package com.example.backend.controller;

import com.example.backend.models.request.HoSoAnNinhMangRequest;
import com.example.backend.models.request.HoSoFilterParams;
import com.example.backend.models.response.HoSoAccessLogResponse;
import com.example.backend.models.response.HoSoAuditLogResponse;
import com.example.backend.models.response.HoSoAnNinhMangResponse;
import com.example.backend.models.response.HoSoFileResponse;
import com.example.backend.service.HoSoAccessLogService;
import com.example.backend.service.HoSoAuditLogService;
import com.example.backend.service.HoSoAnNinhMangService;
import com.example.backend.service.HoSoFileService;
import com.example.backend.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.example.backend.annotation.LogHoSoAccess;

@RestController
@RequestMapping("/api/ho-so-an-ninh-mang")
@RequiredArgsConstructor
public class HoSoAnNinhMangController {

    private static final String HO_SO_TYPE = "AN_MANG";

    private final HoSoAnNinhMangService hoSoAnNinhMangService;
    private final HoSoFileService fileService;
    private final HoSoAccessLogService accessLogService;
    private final HoSoAuditLogService auditLogService;
    private final com.example.backend.service.ExcelExportService excelExportService;
    private final com.example.backend.service.ExcelImportService excelImportService;

    @GetMapping
    @LogHoSoAccess(type = HO_SO_TYPE, action = "VIEW_LIST")
    public ResponseEntity<Page<HoSoAnNinhMangResponse>> getAll(@ModelAttribute HoSoFilterParams filter,
                                                               Pageable pageable,
                                                               HttpServletRequest request) {
        return ResponseEntity.ok(hoSoAnNinhMangService.getAll(filter, pageable));
    }

    @GetMapping("/{id}")
    @LogHoSoAccess(type = HO_SO_TYPE, action = "VIEW_DETAIL")
    public ResponseEntity<HoSoAnNinhMangResponse> getById(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(hoSoAnNinhMangService.getById(id));
    }

    @PostMapping
    public ResponseEntity<HoSoAnNinhMangResponse> create(@Valid @RequestBody HoSoAnNinhMangRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hoSoAnNinhMangService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HoSoAnNinhMangResponse> update(@PathVariable Long id, @Valid @RequestBody HoSoAnNinhMangRequest req) {
        return ResponseEntity.ok(hoSoAnNinhMangService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hoSoAnNinhMangService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FILES
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/files")
    public ResponseEntity<HoSoFileResponse> uploadFile(@PathVariable Long id,
                                                       @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.upload(id, HO_SO_TYPE, file));
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<List<HoSoFileResponse>> getFiles(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFilesByHoSo(id, HO_SO_TYPE));
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/files/{fileId}/download")
    @LogHoSoAccess(type = HO_SO_TYPE, action = "DOWNLOAD_FILE")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @PathVariable Long id, 
            @PathVariable Long fileId, 
            HttpServletRequest request) {
        hoSoAnNinhMangService.getById(id);
        com.example.backend.entity.HoSoFile hoSoFile = fileService.getFileForDownload(fileId);
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(hoSoFile.getFilePath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) throw new com.example.backend.exception.ResourceNotFoundException("File vật lý không tồn tại");
            String encodedFileName = java.net.URLEncoder.encode(hoSoFile.getFileName(), java.nio.charset.StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, hoSoFile.getFileType() != null ? hoSoFile.getFileType() : "application/octet-stream")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file đính kèm: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOGS
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/access-log")
    public ResponseEntity<List<HoSoAccessLogResponse>> getAccessLogs(@PathVariable Long id) {
        return ResponseEntity.ok(accessLogService.getLogsByHoSo(id, HO_SO_TYPE));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<List<HoSoAuditLogResponse>> getAuditLogs(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.getAuditsByRecord(id, HO_SO_TYPE));
    }

    @GetMapping("/export")
    @LogHoSoAccess(type = HO_SO_TYPE, action = "EXPORT")
    public ResponseEntity<byte[]> export(@ModelAttribute HoSoFilterParams filter, HttpServletRequest request) {
        
        Pageable unpaged = org.springframework.data.domain.Pageable.unpaged();
        List<HoSoAnNinhMangResponse> list = hoSoAnNinhMangService.getAll(filter, unpaged).getContent();
        byte[] excelBytes = excelExportService.exportHoSoAnNinhMang(list);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=an-ninh-mang.xlsx")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelBytes);
    }

    @PostMapping("/import/validate")
    public ResponseEntity<com.example.backend.models.response.ImportPreviewResponse<HoSoAnNinhMangRequest>> importValidate(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(excelImportService.validateHoSoAnNinhMang(file));
    }

    @PostMapping("/import/confirm")
    public ResponseEntity<Void> importConfirm(
            @RequestBody java.util.List<HoSoAnNinhMangRequest> validRequests) {
        excelImportService.confirmHoSoAnNinhMang(validRequests);
        return ResponseEntity.ok().build();
    }
}
