package com.example.backend.service;

import com.example.backend.models.response.HoSoFileResponse;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.entity.HoSoFile;
import java.util.List;

public interface HoSoFileService {
    /** Upload file đính kèm cho hồ sơ. Validate size ≤ 50MB, count ≤ 10 file, mime type whitelist */
    HoSoFileResponse upload(Long hoSoId, String hoSoType, MultipartFile file);

    /** Soft delete file đính kèm */
    void deleteFile(Long fileId);

    /** Lấy danh sách file của 1 hồ sơ */
    List<HoSoFileResponse> getFilesByHoSo(Long hoSoId, String hoSoType);

    /** Lấy thông tin entity HoSoFile để download (bao gồm check quyền truy cập) */
    HoSoFile getFileForDownload(Long fileId);
}
