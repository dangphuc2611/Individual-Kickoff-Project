import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface DonViResponse {
  id: number;
  maDonVi: string;
  tenDonVi: string;
  capDonVi: string;
  parentId?: number;
  parentName?: string;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class DonViService {
  private apiUrl = `${environment.apiUrl}/api/don-vi`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<DonViResponse[]> {
    return this.http.get<DonViResponse[]>(this.apiUrl);
  }

  getById(id: number): Observable<DonViResponse> {
    return this.http.get<DonViResponse>(`${this.apiUrl}/${id}`);
  }
}
