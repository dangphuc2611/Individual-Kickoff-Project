package com.example.backend.service.impl;

import com.example.backend.models.response.HoSoAnNinhMangResponse;
import com.example.backend.models.response.HoSoDieuTraResponse;
import com.example.backend.models.response.ThongTinHinhSuResponse;
import com.example.backend.service.ExcelExportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public byte[] exportHoSoDieuTra(List<HoSoDieuTraResponse> data) {
        String[] headers = {
                "STT", "Mã Hồ Sơ", "Tiêu Đề", "Phân Loại", "Mức Độ Mật",
                "Đối Tượng", "Đơn Vị Đối Tượng", "Ngày Mở", "Đơn Vị Quản Lý", "Trạng Thái"
        };
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Hồ Sơ Điều Tra");
            createHeader(workbook, sheet, headers);

            int rowNum = 1;
            for (int i = 0; i < data.size(); i++) {
                HoSoDieuTraResponse item = data.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(safeString(item.getMaHoSo()));
                row.createCell(2).setCellValue(safeString(item.getTieuDe()));
                row.createCell(3).setCellValue(safeString(item.getPhanLoai()));
                row.createCell(4).setCellValue(safeString(item.getMucDoMat()));
                row.createCell(5).setCellValue(safeString(item.getDoiTuongHoTen()));
                row.createCell(6).setCellValue(safeString(item.getDonViDoiTuong()));
                row.createCell(7).setCellValue(item.getNgayMoHoSo() != null ? item.getNgayMoHoSo().toString() : "");
                row.createCell(8).setCellValue(safeString(item.getTenDonVi()));
                row.createCell(9).setCellValue(safeString(item.getTrangThai()));
            }

            return writeToBytes(workbook);
        } catch (IOException e) {
            log.error("Lỗi khi export HoSoDieuTra", e);
            throw new RuntimeException("Lỗi export dữ liệu");
        }
    }

    @Override
    public byte[] exportThongTinHinhSu(List<ThongTinHinhSuResponse> data) {
        String[] headers = {
                "STT", "Mã Thông Tin", "Tiêu Đề", "Loại Tội Danh", "Mức Độ Mật",
                "Đối Tượng Liên Quan", "Đơn Vị Liên Quan", "Ngày Xảy Ra", "Đơn Vị Quản Lý", "Kết Quả Xử Lý"
        };
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Thông Tin Hình Sự");
            createHeader(workbook, sheet, headers);

            int rowNum = 1;
            for (int i = 0; i < data.size(); i++) {
                ThongTinHinhSuResponse item = data.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(safeString(item.getMaThongTin()));
                row.createCell(2).setCellValue(safeString(item.getTieuDe()));
                row.createCell(3).setCellValue(safeString(item.getLoaiToiDanh()));
                row.createCell(4).setCellValue(safeString(item.getMucDoMat()));
                row.createCell(5).setCellValue(safeString(item.getDoiTuongLienQuan()));
                row.createCell(6).setCellValue(safeString(item.getDonViLienQuan()));
                row.createCell(7).setCellValue(item.getNgayXayRa() != null ? item.getNgayXayRa().toString() : "");
                row.createCell(8).setCellValue(safeString(item.getTenDonVi()));
                row.createCell(9).setCellValue(safeString(item.getKetQuaXuLy()));
            }

            return writeToBytes(workbook);
        } catch (IOException e) {
            log.error("Lỗi khi export ThongTinHinhSu", e);
            throw new RuntimeException("Lỗi export dữ liệu");
        }
    }

    @Override
    public byte[] exportHoSoAnNinhMang(List<HoSoAnNinhMangResponse> data) {
        String[] headers = {
                "STT", "Mã Hồ Sơ", "Tiêu Đề", "Loại Tấn Công", "Mức Độ Mật",
                "Hệ Thống Bị Ảnh Hưởng", "Mức Độ Thiệt Hại", "Ngày Phát Hiện", "Đơn Vị Quản Lý", "Trạng Thái"
        };
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Hồ Sơ AN Mạng");
            createHeader(workbook, sheet, headers);

            int rowNum = 1;
            for (int i = 0; i < data.size(); i++) {
                HoSoAnNinhMangResponse item = data.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(safeString(item.getMaHoSo()));
                row.createCell(2).setCellValue(safeString(item.getTieuDe()));
                row.createCell(3).setCellValue(safeString(item.getLoaiTanCong()));
                row.createCell(4).setCellValue(safeString(item.getMucDoMat()));
                row.createCell(5).setCellValue(safeString(item.getHeThongBiAnhHuong()));
                row.createCell(6).setCellValue(safeString(item.getMucDoThietHai()));
                row.createCell(7).setCellValue(item.getNgayPhatHien() != null ? item.getNgayPhatHien().toString() : "");
                row.createCell(8).setCellValue(safeString(item.getTenDonVi()));
                row.createCell(9).setCellValue(safeString(item.getTrangThai()));
            }

            return writeToBytes(workbook);
        } catch (IOException e) {
            log.error("Lỗi khi export HoSoAnNinhMang", e);
            throw new RuntimeException("Lỗi export dữ liệu");
        }
    }

    private void createHeader(Workbook workbook, Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, 5000);
        }
    }

    private String safeString(String val) {
        return val == null ? "" : val;
    }

    private byte[] writeToBytes(SXSSFWorkbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.dispose(); 
        return out.toByteArray();
    }
}
