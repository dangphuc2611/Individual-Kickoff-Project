package com.example.backend.service;

import com.example.backend.models.request.DonViRequest;
import com.example.backend.models.response.DonViResponse;

import java.util.List;

public interface DonViService {
    List<DonViResponse> getAll();
    DonViResponse getById(Long id);
    DonViResponse create(DonViRequest request);
    DonViResponse update(Long id, DonViRequest request);
}
