package com.example.backend.service.impl;

import com.example.backend.entity.HoSoFile;
import com.example.backend.entity.User;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.models.response.HoSoFileResponse;
import com.example.backend.repository.HoSoFileRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.SecurityUtils;
import com.example.backend.service.HoSoAuditLogService;
import com.example.backend.service.HoSoFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service upload / quản lý file đính kèm hồ sơ.
 *
 * Validate:
 *   - Size ≤ 50MB (52_428_800 bytes)
 *   - Số file ≤ 10 file / hồ sơ
 *   - Mime type: chỉ chấp nhận whitelist (PDF, DOC, DOCX, XLS, XLSX, JPG, PNG)
 *
 * Lưu file local tại {app.file.upload-dir}/{hoSoType}/{hoSoId}/
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HoSoFileServiceImpl implements HoSoFileService {

    private static final long MAX_FILE_SIZE = 52_428_800L; // 50 MB
    private static final int MAX_FILE_COUNT = 10;
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "image/jpeg",
            "image/png"
    );

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    private final HoSoFileRepository fileRepository;
    private final UserRepository userRepository;
    private final HoSoAuditLogService auditLogService;

    @Override
    @Transactional
    public HoSoFileResponse upload(Long hoSoId, String hoSoType, MultipartFile file) {
        // Validate size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File vượt quá giới hạn 50MB. Kích thước thực: " +
                    (file.getSize() / 1024 / 1024) + "MB");
        }

        // Validate mime type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BadRequestException("Loại file không được phép. Chỉ chấp nhận: PDF, DOC, DOCX, XLS, XLSX, JPG, PNG");
        }

        // Validate count
        long currentCount = fileRepository.countByHoSoIdAndHoSoTypeAndIsDeletedFalse(hoSoId, hoSoType);
        if (currentCount >= MAX_FILE_COUNT) {
            throw new BadRequestException("Hồ sơ đã đạt giới hạn tối đa " + MAX_FILE_COUNT + " file đính kèm");
        }

        // Lưu file vào local storage
        String savedPath = saveToStorage(hoSoId, hoSoType, file);

        User uploader = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        HoSoFile entity = HoSoFile.builder()
                .hoSoId(hoSoId)
                .hoSoType(hoSoType)
                .fileName(file.getOriginalFilename())
                .filePath(savedPath)
                .fileType(contentType)
                .fileSize(file.getSize())
                .uploadedBy(uploader)
                .uploadedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        HoSoFile saved = fileRepository.save(entity);

        // Ghi audit log upload
        auditLogService.logAudit(hoSoId, hoSoType, "UPLOAD_FILE",
                "file_name", null, file.getOriginalFilename());

        log.info("Upload file '{}' cho hoSoId={} type={} by userId={}",
                file.getOriginalFilename(), hoSoId, hoSoType, uploader.getId());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId) {
        HoSoFile fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy file ID: " + fileId));

        if (fileEntity.isDeleted()) {
            throw new BadRequestException("File này đã bị xóa");
        }

        // Chỉ uploader hoặc CBCT cùng đơn vị mới được xóa file
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!SecurityUtils.isThuTruong() &&
                !fileEntity.getUploadedBy().getId().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền xóa file này");
        }

        fileEntity.setDeleted(true);
        fileRepository.save(fileEntity);

        auditLogService.logAudit(fileEntity.getHoSoId(), fileEntity.getHoSoType(),
                "DELETE_FILE", "file_name", fileEntity.getFileName(), null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoSoFileResponse> getFilesByHoSo(Long hoSoId, String hoSoType) {
        return fileRepository.findByHoSoIdAndHoSoTypeAndIsDeletedFalse(hoSoId, hoSoType)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HoSoFile getFileForDownload(Long fileId) {
        HoSoFile fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy file ID: " + fileId));
        if (fileEntity.isDeleted()) {
            throw new BadRequestException("File này đã bị xóa");
        }
        return fileEntity;
    }

    private String saveToStorage(Long hoSoId, String hoSoType, MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir, hoSoType.toLowerCase(), hoSoId.toString());
            Files.createDirectories(dir);

            String extension = getExtension(file.getOriginalFilename());
            String uniqueName = UUID.randomUUID() + extension;
            Path target = dir.resolve(uniqueName);
            Files.copy(file.getInputStream(), target);

            return target.toString();
        } catch (IOException e) {
            throw new BadRequestException("Lỗi khi lưu file: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    private HoSoFileResponse toResponse(HoSoFile f) {
        return HoSoFileResponse.builder()
                .id(f.getId())
                .hoSoId(f.getHoSoId())
                .hoSoType(f.getHoSoType())
                .fileName(f.getFileName())
                .filePath(f.getFilePath())
                .fileType(f.getFileType())
                .fileSize(f.getFileSize())
                .uploadedById(f.getUploadedBy() != null ? f.getUploadedBy().getId() : null)
                .uploadedByName(f.getUploadedBy() != null ? f.getUploadedBy().getName() : null)
                .uploadedAt(f.getUploadedAt())
                .build();
    }
}
