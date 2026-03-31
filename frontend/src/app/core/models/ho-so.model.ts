export interface HoSoDieuTraResponse {
  id: number;
  maHoSo: string;
  tieuDe: string;
  phanLoai: string;
  mucDoMat: string;
  doiTuongHoTen: string;
  donViDoiTuong: string;
  ngayMoHoSo: string;
  noiDung: string;
  trangThai: string;
  ghiChu: string;
  donViId: number;
  tenDonVi?: string;
  cbctPhuTrachId?: number;
  cbctPhuTrachName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ThongTinHinhSuResponse {
  id: number;
  maThongTin: string;
  tieuDe: string;
  loaiToiDanh: string;
  mucDoMat: string;
  doiTuongLienQuan: string;
  donViLienQuan?: string;
  diaDiem?: string;
  ngayXayRa: string;
  moTaDienBien: string;
  ketQuaXuLy: string;
  donViId: number;
  tenDonVi?: string;
  cbctPhuTrachId?: number;
  cbctPhuTrachName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface HoSoAnNinhMangResponse {
  id: number;
  maHoSo: string;
  tieuDe: string;
  loaiTanCong: string;
  mucDoMat: string;
  heThongBiAnhHuong: string;
  mucDoThietHai?: string;
  ngayPhatHien: string;
  moTaChiTiet: string;
  trangThai: string;
  donViId: number;
  tenDonVi?: string;
  cbctPhuTrachId?: number;
  cbctPhuTrachName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
