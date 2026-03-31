package com.example.backend.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "don_vi")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DonVi {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ma_don_vi")
  private String maDonVi;

  @Column(name = "ten_don_vi")
  private String tenDonVi;

  @Column(name = "cap_don_vi")
  private String capDonVi;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private DonVi parent;

  @Column(name = "is_active")
  private boolean isActive;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
