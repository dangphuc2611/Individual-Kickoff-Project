package com.example.backend.specification;

import com.example.backend.entity.ThongTinHinhSu;
import com.example.backend.models.request.HoSoFilterParams;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification builder cho Thông tin hình sự - tội phạm.
 * Fulltext: maThongTin, tieuDe, doiTuongLienQuan.
 * Filter đặc thù: loaiToiDanh (qua params.getLoai()).
 */
public class ThongTinHinhSuSpecification {

    private ThongTinHinhSuSpecification() {}

    public static Specification<ThongTinHinhSu> withFilter(HoSoFilterParams params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (StringUtils.hasText(params.getSearch())) {
                String pattern = "%" + params.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maThongTin")), pattern),
                        cb.like(cb.lower(root.get("tieuDe")), pattern),
                        cb.like(cb.lower(root.get("doiTuongLienQuan")), pattern)
                ));
            }

            if (StringUtils.hasText(params.getMucDoMat())) {
                predicates.add(cb.equal(root.get("mucDoMat"), params.getMucDoMat()));
            }

            // ketQuaXuLy là trường tương đương trangThai của nhóm B
            if (StringUtils.hasText(params.getTrangThai())) {
                predicates.add(cb.equal(root.get("ketQuaXuLy"), params.getTrangThai()));
            }

            // Loại tội danh (loai param)
            if (StringUtils.hasText(params.getLoai())) {
                predicates.add(cb.equal(root.get("loaiToiDanh"), params.getLoai()));
            }

            if (!CollectionUtils.isEmpty(params.getDonViIds())) {
                predicates.add(root.get("donVi").get("id").in(params.getDonViIds()));
            }

            if (params.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayXayRa"), params.getDateFrom()));
            }
            if (params.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayXayRa"), params.getDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
