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
@Table(name = "thong_tin_hinh_su")
@SQLDelete(sql = "UPDATE thong_tin_hinh_su SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ThongTinHinhSu {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ma_thong_tin")
  private String maThongTin;

  @Column(name = "tieu_de")
  private String tieuDe;

  @Column(name = "loai_toi_danh")
  private String loaiToiDanh;

  @Column(name = "muc_do_mat")
  private String mucDoMat;

  @Column(name = "doi_tuong_lien_quan")
  private String doiTuongLienQuan;

  @Column(name = "don_vi_lien_quan")
  private String donViLienQuan;

  @Column(name = "ngay_xay_ra")
  private LocalDate ngayXayRa;

  @Column(name = "dia_diem")
  private String diaDiem;

  @Column(name = "mo_ta_dien_bien")
  private String moTaDienBien;

  @Column(name = "ket_qua_xu_ly")
  private String ketQuaXuLy;

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