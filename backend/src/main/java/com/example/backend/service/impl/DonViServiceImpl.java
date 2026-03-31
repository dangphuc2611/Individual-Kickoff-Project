package com.example.backend.service.impl;

import com.example.backend.entity.DonVi;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.models.request.DonViRequest;
import com.example.backend.models.response.DonViResponse;
import com.example.backend.repository.DonViRepository;
import com.example.backend.service.DonViService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonViServiceImpl implements DonViService {

    private final DonViRepository donViRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DonViResponse> getAll() {
        return donViRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DonViResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional
    public DonViResponse create(DonViRequest req) {
        if (donViRepository.existsByMaDonVi(req.getMaDonVi())) {
            throw new BadRequestException("Mã đơn vị đã tồn tại: " + req.getMaDonVi());
        }

        DonVi parent = null;
        if (req.getParentId() != null) {
            parent = findOrThrow(req.getParentId());
        }

        DonVi entity = new DonVi();
        entity.setMaDonVi(req.getMaDonVi());
        entity.setTenDonVi(req.getTenDonVi());
        entity.setCapDonVi(req.getCapDonVi());
        entity.setParent(parent);
        entity.setActive(req.getIsActive() != null ? req.getIsActive() : true);
        entity.setCreatedAt(LocalDateTime.now());

        return toResponse(donViRepository.save(entity));
    }

    @Override
    @Transactional
    public DonViResponse update(Long id, DonViRequest req) {
        DonVi entity = findOrThrow(id);

        if (!entity.getMaDonVi().equals(req.getMaDonVi()) &&
                donViRepository.existsByMaDonVi(req.getMaDonVi())) {
            throw new BadRequestException("Mã đơn vị đã tồn tại: " + req.getMaDonVi());
        }

        entity.setMaDonVi(req.getMaDonVi());
        entity.setTenDonVi(req.getTenDonVi());
        entity.setCapDonVi(req.getCapDonVi());
        if (req.getIsActive() != null) entity.setActive(req.getIsActive());

        return toResponse(donViRepository.save(entity));
    }

    private DonVi findOrThrow(Long id) {
        return donViRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị ID: " + id));
    }

    private DonViResponse toResponse(DonVi e) {
        return DonViResponse.builder()
                .id(e.getId())
                .maDonVi(e.getMaDonVi())
                .tenDonVi(e.getTenDonVi())
                .capDonVi(e.getCapDonVi())
                .parentId(e.getParent() != null ? e.getParent().getId() : null)
                .parentName(e.getParent() != null ? e.getParent().getTenDonVi() : null)
                .isActive(e.isActive())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
