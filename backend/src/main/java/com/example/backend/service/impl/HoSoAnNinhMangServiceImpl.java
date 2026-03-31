package com.example.backend.service.impl;

import com.example.backend.common.MaHoSoGenerator;
import com.example.backend.entity.DonVi;
import com.example.backend.entity.HoSoAnNinhMang;
import com.example.backend.entity.User;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.models.request.HoSoAnNinhMangRequest;
import com.example.backend.models.request.HoSoFilterParams;
import com.example.backend.models.response.HoSoAnNinhMangResponse;
import com.example.backend.repository.DonViRepository;
import com.example.backend.repository.HoSoAnNinhMangRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.SecurityUtils;
import com.example.backend.service.HoSoAnNinhMangService;
import com.example.backend.service.HoSoAuditLogService;
import com.example.backend.specification.HoSoAnNinhMangSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HoSoAnNinhMangServiceImpl implements HoSoAnNinhMangService {

    private static final String HO_SO_TYPE = "AN_MANG";

    private final HoSoAnNinhMangRepository repository;
    private final DonViRepository donViRepository;
    private final UserRepository userRepository;
    private final MaHoSoGenerator maHoSoGenerator;
    private final HoSoAuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Page<HoSoAnNinhMangResponse> getAll(HoSoFilterParams filter, Pageable pageable) {
        enforceUnitFilter(filter);
        return repository.findAll(HoSoAnNinhMangSpecification.withFilter(filter), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public HoSoAnNinhMangResponse getById(Long id) {
        HoSoAnNinhMang entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());
        checkClassificationAccess(entity.getMucDoMat());
        return toResponse(entity);
    }

    @Override
    @Transactional
    public HoSoAnNinhMangResponse create(HoSoAnNinhMangRequest req) {
        DonVi donVi = findDonViOrThrow(req.getDonViId());
        checkUnitAccess(donVi);

        User creator = findUserOrThrow(SecurityUtils.getCurrentUserId());
        String maHoSo = maHoSoGenerator.generateAnNinhMang();

        HoSoAnNinhMang entity = HoSoAnNinhMang.builder()
                .maHoSo(maHoSo)
                .tieuDe(req.getTieuDe())
                .loaiTanCong(req.getLoaiTanCong())
                .mucDoMat(req.getMucDoMat())
                .heThongBiAnhHuong(req.getHeThongBiAnhHuong())
                .mucDoThietHai(req.getMucDoThietHai())
                .ngayPhatHien(req.getNgayPhatHien())
                .moTaChiTiet(req.getMoTaChiTiet())
                .trangThai(req.getTrangThai() != null ? req.getTrangThai() : "DANG_XU_LY")
                .donVi(donVi)
                .isDeleted(false)
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build();

        HoSoAnNinhMang saved = repository.save(entity);
        auditLogService.logAudit(saved.getId(), HO_SO_TYPE, "CREATE", "ma_ho_so", null, saved.getMaHoSo());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public HoSoAnNinhMangResponse update(Long id, HoSoAnNinhMangRequest req) {
        HoSoAnNinhMang entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());

        Map<String, String[]> changes = buildDiff(entity, req);
        User updater = findUserOrThrow(SecurityUtils.getCurrentUserId());

        entity.setTieuDe(req.getTieuDe());
        entity.setLoaiTanCong(req.getLoaiTanCong());
        entity.setMucDoMat(req.getMucDoMat());
        entity.setHeThongBiAnhHuong(req.getHeThongBiAnhHuong());
        entity.setMucDoThietHai(req.getMucDoThietHai());
        entity.setNgayPhatHien(req.getNgayPhatHien());
        entity.setMoTaChiTiet(req.getMoTaChiTiet());
        entity.setTrangThai(req.getTrangThai());
        entity.setUpdatedBy(updater);
        entity.setUpdatedAt(LocalDateTime.now());

        HoSoAnNinhMang saved = repository.save(entity);
        auditLogService.logAuditBatch(saved.getId(), HO_SO_TYPE, "UPDATE", changes);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        HoSoAnNinhMang entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());

        User deleter = findUserOrThrow(SecurityUtils.getCurrentUserId());
        entity.setDeleted(true);
        entity.setUpdatedBy(deleter);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);

        auditLogService.logAudit(id, HO_SO_TYPE, "DELETE", "is_deleted", "false", "true");
    }

    // ─────────────── Helpers ───────────────

    private HoSoAnNinhMang findActiveOrThrow(Long id) {
        return repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ AN mạng ID: " + id));
    }

    private DonVi findDonViOrThrow(Long donViId) {
        return donViRepository.findById(donViId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị ID: " + donViId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user ID: " + userId));
    }

    private void checkUnitAccess(DonVi donVi) {
        if (SecurityUtils.isThuTruong()) return;
        Long userDonViId = SecurityUtils.getCurrentUserDonViId();
        if (userDonViId == null || !userDonViId.equals(donVi.getId())) {
            throw new ForbiddenException("Bạn không có quyền truy cập hồ sơ của đơn vị này");
        }
    }

    private void checkClassificationAccess(String mucDoMat) {
        if ("TOI_MAT".equals(mucDoMat) && !SecurityUtils.isThuTruong()) {
            throw new ForbiddenException("Bạn không có quyền xem hồ sơ có mức độ Tối mật");
        }
    }

    private void enforceUnitFilter(HoSoFilterParams filter) {
        if (!SecurityUtils.isThuTruong() && (filter.getDonViIds() == null || filter.getDonViIds().isEmpty())) {
            Long donViId = SecurityUtils.getCurrentUserDonViId();
            if (donViId != null) filter.setDonViIds(List.of(donViId));
        }
    }

    private Map<String, String[]> buildDiff(HoSoAnNinhMang old, HoSoAnNinhMangRequest req) {
        Map<String, String[]> changes = new LinkedHashMap<>();
        putIfChanged(changes, "tieu_de", old.getTieuDe(), req.getTieuDe());
        putIfChanged(changes, "loai_tan_cong", old.getLoaiTanCong(), req.getLoaiTanCong());
        putIfChanged(changes, "muc_do_mat", old.getMucDoMat(), req.getMucDoMat());
        putIfChanged(changes, "he_thong_bi_anh_huong", old.getHeThongBiAnhHuong(), req.getHeThongBiAnhHuong());
        putIfChanged(changes, "muc_do_thiet_hai", old.getMucDoThietHai(), req.getMucDoThietHai());
        putIfChanged(changes, "ngay_phat_hien",
                old.getNgayPhatHien() != null ? old.getNgayPhatHien().toString() : null,
                req.getNgayPhatHien() != null ? req.getNgayPhatHien().toString() : null);
        putIfChanged(changes, "mo_ta_chi_tiet", old.getMoTaChiTiet(), req.getMoTaChiTiet());
        putIfChanged(changes, "trang_thai", old.getTrangThai(), req.getTrangThai());
        return changes;
    }

    private void putIfChanged(Map<String, String[]> map, String field, String oldVal, String newVal) {
        if (!Objects.equals(oldVal, newVal)) map.put(field, new String[]{oldVal, newVal});
    }

    private HoSoAnNinhMangResponse toResponse(HoSoAnNinhMang e) {
        return HoSoAnNinhMangResponse.builder()
                .id(e.getId())
                .maHoSo(e.getMaHoSo())
                .tieuDe(e.getTieuDe())
                .loaiTanCong(e.getLoaiTanCong())
                .mucDoMat(e.getMucDoMat())
                .heThongBiAnhHuong(e.getHeThongBiAnhHuong())
                .mucDoThietHai(e.getMucDoThietHai())
                .ngayPhatHien(e.getNgayPhatHien())
                .moTaChiTiet(e.getMoTaChiTiet())
                .trangThai(e.getTrangThai())
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
