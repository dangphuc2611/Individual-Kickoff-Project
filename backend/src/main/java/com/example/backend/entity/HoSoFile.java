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
import lombok.ToString;

@Entity
@Table(name = "ho_so_file")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class HoSoFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ho_so_id")
  private Long hoSoId;

  @Column(name = "ho_so_type")
  private String hoSoType;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "file_path")
  private String filePath;

  @Column(name = "file_type")
  private String fileType;

  @Column(name = "file_size")
  private Long fileSize;

  @ManyToOne
  @JoinColumn(name = "uploaded_by")
  private User uploadedBy;

  @Column(name = "uploaded_at")
  private LocalDateTime uploadedAt;

  @Column(name = "is_deleted")
  private boolean isDeleted;
}