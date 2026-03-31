import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { ThongTinHinhSuResponse, PageResponse } from '../models/ho-so.model';

@Injectable({
  providedIn: 'root'
})
export class ThongTinHinhSuService {
  private apiUrl = `${environment.apiUrl}/api/thong-tin-hinh-su`;

  constructor(private http: HttpClient) {}

  getAll(page: number, size: number, filters: any = {}): Observable<PageResponse<ThongTinHinhSuResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    if (filters.search) params = params.set('search', filters.search);
    if (filters.loaiToiDanh) params = params.set('loaiToiDanh', filters.loaiToiDanh);
    if (filters.mucDoMat) params = params.set('mucDoMat', filters.mucDoMat);
    if (filters.ketQuaXuLy) params = params.set('ketQuaXuLy', filters.ketQuaXuLy);
    if (filters.donViId) params = params.set('donViIds', filters.donViId);
    if (filters.dateFrom) params = params.set('dateFrom', filters.dateFrom);
    if (filters.dateTo) params = params.set('dateTo', filters.dateTo);
    
    return this.http.get<PageResponse<ThongTinHinhSuResponse>>(this.apiUrl, { params });
  }

  getById(id: number): Observable<ThongTinHinhSuResponse> {
    return this.http.get<ThongTinHinhSuResponse>(`${this.apiUrl}/${id}`);
  }

  create(hoSo: any): Observable<ThongTinHinhSuResponse> {
    return this.http.post<ThongTinHinhSuResponse>(this.apiUrl, hoSo);
  }

  update(id: number, hoSo: any): Observable<ThongTinHinhSuResponse> {
    return this.http.put<ThongTinHinhSuResponse>(`${this.apiUrl}/${id}`, hoSo);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAuditLogs(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/audit`);
  }

  getAccessLogs(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/access-log`);
  }

  exportExcel(filters: any = {}): Observable<Blob> {
    let params = new HttpParams();
    Object.keys(filters).forEach(k => {
      if (filters[k]) params = params.set(k, filters[k]);
    });
    return this.http.get(`${this.apiUrl}/export`, { params, responseType: 'blob' });
  }

  getFiles(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/files`);
  }

  downloadFile(id: number, fileId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/files/${fileId}/download`, { responseType: 'blob' });
  }

  validateImport(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/import/validate`, formData);
  }

  confirmImport(validRequests: any[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/import/confirm`, validRequests);
  }
}
