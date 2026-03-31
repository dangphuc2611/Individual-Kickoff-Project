package com.example.backend.service;

import com.example.backend.models.request.HoSoAnNinhMangRequest;
import com.example.backend.models.request.HoSoFilterParams;
import com.example.backend.models.response.HoSoAnNinhMangResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HoSoAnNinhMangService {
    Page<HoSoAnNinhMangResponse> getAll(HoSoFilterParams filter, Pageable pageable);
    HoSoAnNinhMangResponse getById(Long id);
    HoSoAnNinhMangResponse create(HoSoAnNinhMangRequest request);
    HoSoAnNinhMangResponse update(Long id, HoSoAnNinhMangRequest request);
    void delete(Long id);
}
