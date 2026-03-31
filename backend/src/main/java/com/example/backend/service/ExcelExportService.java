package com.example.backend.service;

import com.example.backend.models.response.HoSoAnNinhMangResponse;
import com.example.backend.models.response.HoSoDieuTraResponse;
import com.example.backend.models.response.ThongTinHinhSuResponse;

import java.util.List;

public interface ExcelExportService {
    byte[] exportHoSoDieuTra(List<HoSoDieuTraResponse> data);
    byte[] exportThongTinHinhSu(List<ThongTinHinhSuResponse> data);
    byte[] exportHoSoAnNinhMang(List<HoSoAnNinhMangResponse> data);
}
