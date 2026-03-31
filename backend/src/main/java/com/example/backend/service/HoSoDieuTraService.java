package com.example.backend.service;

import com.example.backend.models.request.HoSoDieuTraRequest;
import com.example.backend.models.request.HoSoFilterParams;
import com.example.backend.models.response.HoSoDieuTraResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HoSoDieuTraService {
    Page<HoSoDieuTraResponse> getAll(HoSoFilterParams filter, Pageable pageable);
    HoSoDieuTraResponse getById(Long id);
    HoSoDieuTraResponse create(HoSoDieuTraRequest request);
    HoSoDieuTraResponse update(Long id, HoSoDieuTraRequest request);
    void delete(Long id);
}
