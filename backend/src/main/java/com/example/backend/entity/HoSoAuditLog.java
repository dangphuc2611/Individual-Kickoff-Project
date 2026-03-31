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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ho_so_audit_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class HoSoAuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "record_id")
  private Long recordId;

  @Column(name = "ho_so_type")
  private String hoSoType;

  @ManyToOne
  @JoinColumn(name = "changed_by")
  private User changedBy;

  @Column(name = "changed_at")
  private LocalDateTime changedAt;

  @Column(name = "action")
  private String action;

  @Column(name = "field_name")
  private String fieldName;

  @Column(name = "old_value")
  private String oldValue;

  @Column(name = "new_value")
  private String newValue;
}