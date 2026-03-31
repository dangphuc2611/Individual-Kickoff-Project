package com.example.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ho_so_an_ninh_mang")
@SQLDelete(sql = "UPDATE ho_so_an_ninh_mang SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
public class HoSoAnNinhMang {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ma_ho_so")
  private String maHoSo;

  @Column(name = "tieu_de")
  private String tieuDe;

  @Column(name = "loai_tan_cong")
  private String loaiTanCong;

  @Column(name = "muc_do_mat")
  private String mucDoMat;

  @Column(name = "he_thong_bi_anh_huong")
  private String heThongBiAnhHuong;

  @Column(name = "muc_do_thiet_hai")
  private String mucDoThietHai;

  @Column(name = "ngay_phat_hien")
  private LocalDate ngayPhatHien;

  @Column(name = "mo_ta_chi_tiet")
  private String moTaChiTiet;

  @Column(name = "trang_thai")
  private String trangThai;

  @ManyToOne
  @JoinColumn(name = "don_vi_id")
  private DonVi donVi;

  @Column(name = "is_deleted")
  private boolean isDeleted;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private User createdBy;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "updated_by")
  private User updatedBy;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
