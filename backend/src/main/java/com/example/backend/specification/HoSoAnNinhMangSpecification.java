package com.example.backend.specification;

import com.example.backend.entity.HoSoAnNinhMang;
import com.example.backend.models.request.HoSoFilterParams;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification builder cho Hồ sơ nghiệp vụ an ninh mạng.
 * Fulltext: maHoSo, tieuDe, heThongBiAnhHuong.
 * Filter đặc thù: loaiTanCong (qua params.getLoai()).
 */
public class HoSoAnNinhMangSpecification {

    private HoSoAnNinhMangSpecification() {}

    public static Specification<HoSoAnNinhMang> withFilter(HoSoFilterParams params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (StringUtils.hasText(params.getSearch())) {
                String pattern = "%" + params.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maHoSo")), pattern),
                        cb.like(cb.lower(root.get("tieuDe")), pattern),
                        cb.like(cb.lower(root.get("heThongBiAnhHuong")), pattern)
                ));
            }

            if (StringUtils.hasText(params.getMucDoMat())) {
                predicates.add(cb.equal(root.get("mucDoMat"), params.getMucDoMat()));
            }

            if (StringUtils.hasText(params.getTrangThai())) {
                predicates.add(cb.equal(root.get("trangThai"), params.getTrangThai()));
            }

            // Loại tấn công (loai param)
            if (StringUtils.hasText(params.getLoai())) {
                predicates.add(cb.equal(root.get("loaiTanCong"), params.getLoai()));
            }

            if (!CollectionUtils.isEmpty(params.getDonViIds())) {
                predicates.add(root.get("donVi").get("id").in(params.getDonViIds()));
            }

            if (params.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayPhatHien"), params.getDateFrom()));
            }
            if (params.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayPhatHien"), params.getDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
