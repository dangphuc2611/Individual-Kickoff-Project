package com.example.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import lombok.ToString;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ho_so_dieu_tra")
@SQLDelete(sql = "UPDATE ho_so_dieu_tra SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class HoSoDieuTra {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ma_ho_so")
  private String maHoSo;

  @Column(name = "tieu_de")
  private String tieuDe;

  @Column(name = "phan_loai")
  private String phanLoai;

  @Column(name = "muc_do_mat")
  private String mucDoMat;

  @Column(name = "doi_tuong_ho_ten")
  private String doiTuongHoTen;

  @Column(name = "don_vi_doi_tuong")
  private String donViDoiTuong;

  @Column(name = "ngay_mo_ho_so")
  private LocalDate ngayMoHoSo;

  @ManyToOne
  @JoinColumn(name = "cbct_phu_trach")
  private User cbctPhuTrach;

  @Column(name = "noi_dung")
  private String noiDung;

  @Column(name = "trang_thai")
  private String trangThai;

  @Column(name = "ghi_chu")
  private String ghiChu;

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