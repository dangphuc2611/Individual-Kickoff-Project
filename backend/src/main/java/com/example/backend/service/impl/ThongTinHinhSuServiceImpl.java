package com.example.backend.service.impl;

import com.example.backend.common.MaHoSoGenerator;
import com.example.backend.entity.DonVi;
import com.example.backend.entity.ThongTinHinhSu;
import com.example.backend.entity.User;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.models.request.HoSoFilterParams;
import com.example.backend.models.request.ThongTinHinhSuRequest;
import com.example.backend.models.response.ThongTinHinhSuResponse;
import com.example.backend.repository.DonViRepository;
import com.example.backend.repository.ThongTinHinhSuRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.SecurityUtils;
import com.example.backend.service.HoSoAuditLogService;
import com.example.backend.service.ThongTinHinhSuService;
import com.example.backend.specification.ThongTinHinhSuSpecification;
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
public class ThongTinHinhSuServiceImpl implements ThongTinHinhSuService {

    private static final String HO_SO_TYPE = "HINH_SU";

    private final ThongTinHinhSuRepository repository;
    private final DonViRepository donViRepository;
    private final UserRepository userRepository;
    private final MaHoSoGenerator maHoSoGenerator;
    private final HoSoAuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Page<ThongTinHinhSuResponse> getAll(HoSoFilterParams filter, Pageable pageable) {
        enforceUnitFilter(filter);
        return repository.findAll(ThongTinHinhSuSpecification.withFilter(filter), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ThongTinHinhSuResponse getById(Long id) {
        ThongTinHinhSu entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());
        checkClassificationAccess(entity.getMucDoMat());
        return toResponse(entity);
    }

    @Override
    @Transactional
    public ThongTinHinhSuResponse create(ThongTinHinhSuRequest req) {
        DonVi donVi = findDonViOrThrow(req.getDonViId());
        checkUnitAccess(donVi);

        User creator = findUserOrThrow(SecurityUtils.getCurrentUserId());
        String maThongTin = maHoSoGenerator.generateHinhSu();

        ThongTinHinhSu entity = ThongTinHinhSu.builder()
                .maThongTin(maThongTin)
                .tieuDe(req.getTieuDe())
                .loaiToiDanh(req.getLoaiToiDanh())
                .mucDoMat(req.getMucDoMat())
                .doiTuongLienQuan(req.getDoiTuongLienQuan())
                .donViLienQuan(req.getDonViLienQuan())
                .ngayXayRa(req.getNgayXayRa())
                .diaDiem(req.getDiaDiem())
                .moTaDienBien(req.getMoTaDienBien())
                .ketQuaXuLy(req.getKetQuaXuLy() != null ? req.getKetQuaXuLy() : "DANG_XU_LY")
                .donVi(donVi)
                .isDeleted(false)
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build();

        ThongTinHinhSu saved = repository.save(entity);
        auditLogService.logAudit(saved.getId(), HO_SO_TYPE, "CREATE", "ma_thong_tin", null, saved.getMaThongTin());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ThongTinHinhSuResponse update(Long id, ThongTinHinhSuRequest req) {
        ThongTinHinhSu entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());

        Map<String, String[]> changes = buildDiff(entity, req);
        User updater = findUserOrThrow(SecurityUtils.getCurrentUserId());

        entity.setTieuDe(req.getTieuDe());
        entity.setLoaiToiDanh(req.getLoaiToiDanh());
        entity.setMucDoMat(req.getMucDoMat());
        entity.setDoiTuongLienQuan(req.getDoiTuongLienQuan());
        entity.setDonViLienQuan(req.getDonViLienQuan());
        entity.setNgayXayRa(req.getNgayXayRa());
        entity.setDiaDiem(req.getDiaDiem());
        entity.setMoTaDienBien(req.getMoTaDienBien());
        entity.setKetQuaXuLy(req.getKetQuaXuLy());
        entity.setUpdatedBy(updater);
        entity.setUpdatedAt(LocalDateTime.now());

        ThongTinHinhSu saved = repository.save(entity);
        auditLogService.logAuditBatch(saved.getId(), HO_SO_TYPE, "UPDATE", changes);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ThongTinHinhSu entity = findActiveOrThrow(id);
        checkUnitAccess(entity.getDonVi());

        User deleter = findUserOrThrow(SecurityUtils.getCurrentUserId());
        entity.setDeleted(true);
        entity.setUpdatedBy(deleter);
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);

        auditLogService.logAudit(id, HO_SO_TYPE, "DELETE", "is_deleted", "false", "true");
    }

    // ─────────────── Helpers ───────────────

    private ThongTinHinhSu findActiveOrThrow(Long id) {
        return repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin hình sự ID: " + id));
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

    private Map<String, String[]> buildDiff(ThongTinHinhSu old, ThongTinHinhSuRequest req) {
        Map<String, String[]> changes = new LinkedHashMap<>();
        putIfChanged(changes, "tieu_de", old.getTieuDe(), req.getTieuDe());
        putIfChanged(changes, "loai_toi_danh", old.getLoaiToiDanh(), req.getLoaiToiDanh());
        putIfChanged(changes, "muc_do_mat", old.getMucDoMat(), req.getMucDoMat());
        putIfChanged(changes, "doi_tuong_lien_quan", old.getDoiTuongLienQuan(), req.getDoiTuongLienQuan());
        putIfChanged(changes, "don_vi_lien_quan", old.getDonViLienQuan(), req.getDonViLienQuan());
        putIfChanged(changes, "ngay_xay_ra",
                old.getNgayXayRa() != null ? old.getNgayXayRa().toString() : null,
                req.getNgayXayRa() != null ? req.getNgayXayRa().toString() : null);
        putIfChanged(changes, "dia_diem", old.getDiaDiem(), req.getDiaDiem());
        putIfChanged(changes, "mo_ta_dien_bien", old.getMoTaDienBien(), req.getMoTaDienBien());
        putIfChanged(changes, "ket_qua_xu_ly", old.getKetQuaXuLy(), req.getKetQuaXuLy());
        return changes;
    }

    private void putIfChanged(Map<String, String[]> map, String field, String oldVal, String newVal) {
        if (!Objects.equals(oldVal, newVal)) map.put(field, new String[]{oldVal, newVal});
    }

    private ThongTinHinhSuResponse toResponse(ThongTinHinhSu e) {
        return ThongTinHinhSuResponse.builder()
                .id(e.getId())
                .maThongTin(e.getMaThongTin())
                .tieuDe(e.getTieuDe())
                .loaiToiDanh(e.getLoaiToiDanh())
                .mucDoMat(e.getMucDoMat())
                .doiTuongLienQuan(e.getDoiTuongLienQuan())
                .donViLienQuan(e.getDonViLienQuan())
                .ngayXayRa(e.getNgayXayRa())
                .diaDiem(e.getDiaDiem())
                .moTaDienBien(e.getMoTaDienBien())
                .ketQuaXuLy(e.getKetQuaXuLy())
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
