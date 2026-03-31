package com.example.backend.service.impl;

import com.example.backend.exception.BadRequestException;
import com.example.backend.models.request.HoSoAnNinhMangRequest;
import com.example.backend.models.request.HoSoDieuTraRequest;
import com.example.backend.models.request.ThongTinHinhSuRequest;
import com.example.backend.models.response.ImportPreviewResponse;
import com.example.backend.models.response.ImportRow;
import com.example.backend.service.ExcelImportService;
import com.example.backend.service.ExcelImportService;
import com.example.backend.service.HoSoDieuTraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelImportServiceImpl implements ExcelImportService {

    private final HoSoDieuTraService hoSoDieuTraService;

    @Override
    public ImportPreviewResponse<HoSoDieuTraRequest> validateHoSoDieuTra(MultipartFile file) {
        List<ImportRow<HoSoDieuTraRequest>> rows = new ArrayList<>();
        int validRows = 0, invalidRows = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                List<String> errors = new ArrayList<>();
                HoSoDieuTraRequest req = new HoSoDieuTraRequest();

                // Lấy thông tin từ cột (Cấu trúc giả định mẫu)
                req.setTieuDe(getStringValue(row.getCell(0)));
                if (req.getTieuDe() == null || req.getTieuDe().isBlank()) errors.add("Thiếu Tiêu đề (cột 1)");

                req.setPhanLoai(getStringValue(row.getCell(1)));
                if (req.getPhanLoai() == null || req.getPhanLoai().isBlank()) errors.add("Thiếu Phân loại (cột 2)");

                req.setMucDoMat(getStringValue(row.getCell(2)));
                if (req.getMucDoMat() == null || req.getMucDoMat().isBlank()) errors.add("Thiếu Mức độ mật (cột 3)");

                req.setDoiTuongHoTen(getStringValue(row.getCell(3)));
                req.setDonViDoiTuong(getStringValue(row.getCell(4)));

                try {
                    Cell dateCell = row.getCell(5);
                    if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC) {
                        req.setNgayMoHoSo(dateCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    } else {
                        req.setNgayMoHoSo(LocalDate.now());
                    }
                } catch (Exception e) {
                    errors.add("Ngày mở hồ sơ không hợp lệ (cột 6)");
                }

                req.setNoiDung(getStringValue(row.getCell(6)));
                if (req.getNoiDung() == null || req.getNoiDung().isBlank()) req.setNoiDung("Nội dung import");

                req.setTrangThai(getStringValue(row.getCell(7)));
                if (req.getTrangThai() == null || req.getTrangThai().isBlank()) req.setTrangThai("DANG_THEO_DOI");

                try {
                    Double donViIdDouble = getNumericValue(row.getCell(8));
                    req.setDonViId(donViIdDouble != null ? donViIdDouble.longValue() : null);
                    if (req.getDonViId() == null) errors.add("Thiếu Đơn vị ID hợp lệ (cột 9)");
                } catch (Exception e) {
                    errors.add("Đơn vị ID phải là số (cột 9)");
                }

                boolean isValid = errors.isEmpty();
                if (isValid) validRows++; else invalidRows++;

                rows.add(ImportRow.<HoSoDieuTraRequest>builder()
                        .rowNum(i + 1)
                        .valid(isValid)
                        .errors(errors)
                        .requestData(req)
                        .build());
            }
        } catch (Exception e) {
            throw new BadRequestException("File không hợp lệ hoặc không đúng định dạng Excel");
        }

        return ImportPreviewResponse.<HoSoDieuTraRequest>builder()
                .totalRows(validRows + invalidRows)
                .validRows(validRows)
                .invalidRows(invalidRows)
                .data(rows)
                .build();
    }

    @Override
    @Transactional
    public void confirmHoSoDieuTra(List<HoSoDieuTraRequest> validRequests) {
        if (validRequests == null || validRequests.isEmpty()) return;
        for (HoSoDieuTraRequest req : validRequests) {
            hoSoDieuTraService.create(req);
        }
    }

    @Override
    public ImportPreviewResponse<ThongTinHinhSuRequest> validateThongTinHinhSu(MultipartFile file) {
        throw new BadRequestException("Chưa hỗ trợ Import bằng Excel cho Thông tin Hình Sự");
    }

    @Override
    public void confirmThongTinHinhSu(List<ThongTinHinhSuRequest> validRequests) {
        throw new BadRequestException("Chưa hỗ trợ Import bằng Excel cho Thông tin Hình Sự");
    }

    @Override
    public ImportPreviewResponse<HoSoAnNinhMangRequest> validateHoSoAnNinhMang(MultipartFile file) {
        throw new BadRequestException("Chưa hỗ trợ Import bằng Excel cho Hồ sơ An Ninh Mạng");
    }

    @Override
    public void confirmHoSoAnNinhMang(List<HoSoAnNinhMangRequest> validRequests) {
        throw new BadRequestException("Chưa hỗ trợ Import bằng Excel cho Hồ sơ An Ninh Mạng");
    }

    // --- Helpers

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return cell.toString();
    }

    private Double getNumericValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (Exception ignored) {}
        }
        return null;
    }
}
