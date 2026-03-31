package com.example.backend.service.impl;

import com.example.backend.common.MaHoSoGenerator;
import com.example.backend.entity.DonVi;
import com.example.backend.entity.HoSoDieuTra;
import com.example.backend.entity.User;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.models.request.HoSoDieuTraRequest;
import com.example.backend.models.request.HoSoFilterParams;
import com.example.backend.models.response.HoSoDieuTraResponse;
import com.example.backend.repository.DonViRepository;
import com.example.backend.repository.HoSoDieuTraRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.SecurityUtils;
import com.example.backend.service.HoSoAuditLogService;
import com.example.backend.service.HoSoDieuTraService;
import com.example.backend.specification.HoSoDieuTraSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service Hồ sơ điều tra cơ bản.
 *
 * Phân quyền đơn vị: user chỉ thao tác hồ sơ thuộc don_vi_id của mình.
 * Phân quyền muc_do_mat: TOI_MAT chỉ THU_TRUONG mới xem được.
 * Soft delete: chỉ set is_deleted = true.
 * Audit log: ghi diff sau mỗi CREATE / UPDATE / DELETE.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HoSoDieuTraServiceImpl implements HoSoDieuTraService {

    private static final String HO_SO_TYPE = "DIEU_TRA";

    private final HoSoDieuTraRepository repository;
    private final DonViRepository donViRepository;
    private final UserRepository userRepository;
    private final MaHoSoGenerator maHoSoGenerator;
    private final HoSoAuditLogService auditLogService;

    // ─────────────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<HoSoDieuTraResponse> getAll(HoSoFilterParams filter, Pageable pageable) {
        // CBCT / TRUONG_PHONG chỉ xem đơn vị mình nếu không truyền donViIds
        enforceUnitFilterForNonAdmin(filter);

        return repository.findAll(HoSoDieuTraSpecification.withFilter(filter), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public HoSoDieuTraResponse getById(Long id) {
        HoSoDieuTra entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());
        checkClassificationAccess(entity.getMucDoMat());
        return toResponse(entity);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public HoSoDieuTraResponse create(HoSoDieuTraRequest req) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        DonVi donVi = findDonViOrThrow(req.getDonViId());
        checkUnitAccess(donVi);

        // Auto-fill cbctPhuTrach = currentUser nếu không truyền
        Long cbctId = req.getCbctPhuTrachId() != null ? req.getCbctPhuTrachId() : currentUserId;
        User cbct = findUserOrThrow(cbctId);
        User creator = findUserOrThrow(currentUserId);

        String maHoSo = maHoSoGenerator.generateDieuTra();

        HoSoDieuTra entity = HoSoDieuTra.builder()
                .maHoSo(maHoSo)
                .tieuDe(req.getTieuDe())
                .phanLoai(req.getPhanLoai())
                .mucDoMat(req.getMucDoMat())
                .doiTuongHoTen(req.getDoiTuongHoTen())
                .donViDoiTuong(req.getDonViDoiTuong())
                .ngayMoHoSo(req.getNgayMoHoSo())
                .cbctPhuTrach(cbct)
                .noiDung(req.getNoiDung())
                .trangThai(req.getTrangThai() != null ? req.getTrangThai() : "DANG_THEO_DOI")
                .ghiChu(req.getGhiChu())
                .donVi(donVi)
                .isDeleted(false)
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build();

        HoSoDieuTra saved = repository.save(entity);

        auditLogService.logAudit(saved.getId(), HO_SO_TYPE, "CREATE",
                "ma_ho_so", null, saved.getMaHoSo());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public HoSoDieuTraResponse update(Long id, HoSoDieuTraRequest req) {
        HoSoDieuTra entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());

        // Build diff map để ghi audit log
        Map<String, String[]> changes = buildDiff(entity, req);

        User updater = findUserOrThrow(SecurityUtils.getCurrentUserId());

        entity.setTieuDe(req.getTieuDe());
        entity.setPhanLoai(req.getPhanLoai());
        entity.setMucDoMat(req.getMucDoMat());
        entity.setDoiTuongHoTen(req.getDoiTuongHoTen());
        entity.setDonViDoiTuong(req.getDonViDoiTuong());
        entity.setNgayMoHoSo(req.getNgayMoHoSo());
        entity.setNoiDung(req.getNoiDung());
        entity.setTrangThai(req.getTrangThai());
        entity.setGhiChu(req.getGhiChu());
        entity.setUpdatedBy(updater);
        entity.setUpdatedAt(LocalDateTime.now());

        if (req.getCbctPhuTrachId() != null) {
            entity.setCbctPhuTrach(findUserOrThrow(req.getCbctPhuTrachId()));
        }

        HoSoDieuTra saved = repository.save(entity);
        auditLogService.logAuditBatch(saved.getId(), HO_SO_TYPE, "UPDATE", changes);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        HoSoDieuTra entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());

        User deleter = findUserOrThrow(SecurityUtils.getCurrentUserId());
        entity.setDeleted(true);
        entity.setUpdatedBy(deleter);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);

        auditLogService.logAudit(id, HO_SO_TYPE, "DELETE", "is_deleted", "false", "true");
        log.info("Soft deleted HoSoDieuTra id={} by userId={}", id, deleter.getId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private HoSoDieuTra findActiveOrThrow(Long id) {
        return repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ điều tra ID: " + id));
    }

    private DonVi findDonViOrThrow(Long donViId) {
        return donViRepository.findById(donViId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị ID: " + donViId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user ID: " + userId));
    }

    /** CBCT / TRUONG_PHONG chỉ được truy cập đơn vị của mình. THU_TRUONG được toàn ngành. */
    private void checkUnitAccess(DonVi donVi) {
        if (SecurityUtils.isThuTruong()) return;
        Long userDonViId = SecurityUtils.getCurrentUserDonViId();
        if (userDonViId == null || !userDonViId.equals(donVi.getId())) {
            throw new ForbiddenException("Bạn không có quyền truy cập hồ sơ của đơn vị này");
        }
    }

    /** TOI_MAT chỉ THU_TRUONG mới xem. */
    private void checkClassificationAccess(String mucDoMat) {
        if ("TOI_MAT".equals(mucDoMat) && !SecurityUtils.isThuTruong()) {
            throw new ForbiddenException("Bạn không có quyền xem hồ sơ có mức độ Tối mật");
        }
    }

    /** CBCT/TRUONG_PHONG: tự động thêm donViId của mình vào filter nếu chưa truyền */
    private void enforceUnitFilterForNonAdmin(HoSoFilterParams filter) {
        if (!SecurityUtils.isThuTruong() && (filter.getDonViIds() == null || filter.getDonViIds().isEmpty())) {
            Long userDonViId = SecurityUtils.getCurrentUserDonViId();
            if (userDonViId != null) {
                filter.setDonViIds(java.util.List.of(userDonViId));
            }
        }
    }

    private Map<String, String[]> buildDiff(HoSoDieuTra old, HoSoDieuTraRequest req) {
        Map<String, String[]> changes = new LinkedHashMap<>();
        putIfChanged(changes, "tieu_de", old.getTieuDe(), req.getTieuDe());
        putIfChanged(changes, "phan_loai", old.getPhanLoai(), req.getPhanLoai());
        putIfChanged(changes, "muc_do_mat", old.getMucDoMat(), req.getMucDoMat());
        putIfChanged(changes, "doi_tuong_ho_ten", old.getDoiTuongHoTen(), req.getDoiTuongHoTen());
        putIfChanged(changes, "don_vi_doi_tuong", old.getDonViDoiTuong(), req.getDonViDoiTuong());
        putIfChanged(changes, "ngay_mo_ho_so",
                old.getNgayMoHoSo() != null ? old.getNgayMoHoSo().toString() : null,
                req.getNgayMoHoSo() != null ? req.getNgayMoHoSo().toString() : null);
        putIfChanged(changes, "noi_dung", old.getNoiDung(), req.getNoiDung());
        putIfChanged(changes, "trang_thai", old.getTrangThai(), req.getTrangThai());
        putIfChanged(changes, "ghi_chu", old.getGhiChu(), req.getGhiChu());
        return changes;
    }

    private void putIfChanged(Map<String, String[]> map, String field, String oldVal, String newVal) {
        if (!java.util.Objects.equals(oldVal, newVal)) {
            map.put(field, new String[]{oldVal, newVal});
        }
    }

    private HoSoDieuTraResponse toResponse(HoSoDieuTra e) {
        return HoSoDieuTraResponse.builder()
                .id(e.getId())
                .maHoSo(e.getMaHoSo())
                .tieuDe(e.getTieuDe())
                .phanLoai(e.getPhanLoai())
                .mucDoMat(e.getMucDoMat())
                .doiTuongHoTen(e.getDoiTuongHoTen())
                .donViDoiTuong(e.getDonViDoiTuong())
                .ngayMoHoSo(e.getNgayMoHoSo())
                .cbctPhuTrachId(e.getCbctPhuTrach() != null ? e.getCbctPhuTrach().getId() : null)
                .cbctPhuTrachName(e.getCbctPhuTrach() != null ? e.getCbctPhuTrach().getName() : null)
                .noiDung(e.getNoiDung())
                .trangThai(e.getTrangThai())
                .ghiChu(e.getGhiChu())
                .donViId(e.getDonVi() != null ? e.getDonVi().getId() : null)
                .tenDonVi(e.getDonVi() != null ? e.getDonVi().getTenDonVi() : null)
                .createdById(e.getCreatedBy() != null ? e.getCreatedBy().getId() : null)
                .createdByName(e.getCreatedBy() != null ? e.getCreatedBy().getName() : null)
                .createdAt(e.getCreatedAt())
                .updatedById(e.getUpdatedBy() != null ? e.getUpdatedBy().getId() : null)
                .updatedByName(e.getUpdatedBy() != null ? e.getUpdatedBy().getName() : null)
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
