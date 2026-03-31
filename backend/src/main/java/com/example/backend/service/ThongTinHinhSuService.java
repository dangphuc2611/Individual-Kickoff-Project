package com.example.backend.service;

import com.example.backend.models.request.HoSoFilterParams;
import com.example.backend.models.request.ThongTinHinhSuRequest;
import com.example.backend.models.response.ThongTinHinhSuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ThongTinHinhSuService {
    Page<ThongTinHinhSuResponse> getAll(HoSoFilterParams filter, Pageable pageable);
    ThongTinHinhSuResponse getById(Long id);
    ThongTinHinhSuResponse create(ThongTinHinhSuRequest request);
    ThongTinHinhSuResponse update(Long id, ThongTinHinhSuRequest request);
    void delete(Long id);
}
