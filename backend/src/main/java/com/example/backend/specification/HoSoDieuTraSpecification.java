package com.example.backend.specification;

import com.example.backend.entity.HoSoDieuTra;
import com.example.backend.models.request.HoSoFilterParams;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification builder cho Hồ sơ điều tra cơ bản.
 * Hỗ trợ filter động theo: search, mucDoMat, trangThai, donViIds[], dateFrom/dateTo.
 * Luôn append điều kiện is_deleted = false.
 */
public class HoSoDieuTraSpecification {

    private HoSoDieuTraSpecification() {}

    public static Specification<HoSoDieuTra> withFilter(HoSoFilterParams params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Luôn lọc chưa bị xóa mềm
            predicates.add(cb.isFalse(root.get("isDeleted")));

            // Fulltext search: maHoSo LIKE, tieuDe LIKE, doiTuongHoTen LIKE
            if (StringUtils.hasText(params.getSearch())) {
                String pattern = "%" + params.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maHoSo")), pattern),
                        cb.like(cb.lower(root.get("tieuDe")), pattern),
                        cb.like(cb.lower(root.get("doiTuongHoTen")), pattern)
                ));
            }

            // Filter theo mức độ mật
            if (StringUtils.hasText(params.getMucDoMat())) {
                predicates.add(cb.equal(root.get("mucDoMat"), params.getMucDoMat()));
            }

            // Filter theo trạng thái
            if (StringUtils.hasText(params.getTrangThai())) {
                predicates.add(cb.equal(root.get("trangThai"), params.getTrangThai()));
            }

            // Filter theo danh sách đơn vị (donViIds[])
            if (!CollectionUtils.isEmpty(params.getDonViIds())) {
                predicates.add(root.get("donVi").get("id").in(params.getDonViIds()));
            }

            // Filter khoảng ngày mở hồ sơ
            if (params.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayMoHoSo"), params.getDateFrom()));
            }
            if (params.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayMoHoSo"), params.getDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
