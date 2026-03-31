package com.example.backend.service;

import com.example.backend.models.request.HoSoAnNinhMangRequest;
import com.example.backend.models.request.HoSoDieuTraRequest;
import com.example.backend.models.request.ThongTinHinhSuRequest;
import com.example.backend.models.response.ImportPreviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelImportService {
    ImportPreviewResponse<HoSoDieuTraRequest> validateHoSoDieuTra(MultipartFile file);
    void confirmHoSoDieuTra(List<HoSoDieuTraRequest> validRequests);

    ImportPreviewResponse<ThongTinHinhSuRequest> validateThongTinHinhSu(MultipartFile file);
    void confirmThongTinHinhSu(List<ThongTinHinhSuRequest> validRequests);

    ImportPreviewResponse<HoSoAnNinhMangRequest> validateHoSoAnNinhMang(MultipartFile file);
    void confirmHoSoAnNinhMang(List<HoSoAnNinhMangRequest> validRequests);
}
