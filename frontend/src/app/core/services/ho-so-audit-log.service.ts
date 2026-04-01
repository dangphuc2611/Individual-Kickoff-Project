import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { PageResponse } from '../models/ho-so.model';

@Injectable({
  providedIn: 'root'
})
export class HoSoAuditLogService {
  private apiUrl = `${environment.apiUrl}/api/ho-so/audit-log`;

  constructor(private http: HttpClient) {}

  getAllLogs(page: number, size: number): Observable<PageResponse<any>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'changedAt,desc');
      
    return this.http.get<PageResponse<any>>(this.apiUrl, { params });
  }
}
