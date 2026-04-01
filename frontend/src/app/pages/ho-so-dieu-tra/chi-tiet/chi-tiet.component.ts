import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { HoSoDieuTraService } from '../../../core/services/ho-so-dieu-tra.service';
import { ActivatedRoute } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { TabsModule } from 'primeng/tabs';
import { TableModule } from 'primeng/table';
import { TimelineModule } from 'primeng/timeline';
import { TagModule } from 'primeng/tag';
import { SafeHtmlPipe } from '../../../shared/pipes/safe-html.pipe';
import { FileUploadComponent } from '../../../shared/components/file-upload/file-upload.component';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-chi-tiet-dieu-tra',
  standalone: true,
  imports: [CommonModule, ButtonModule, TabsModule, TableModule, TimelineModule, TagModule, SafeHtmlPipe, FileUploadComponent],
  template: `
    <div class="bg-slate-50 min-h-screen pb-12">
      <!-- Loading State -->
      <div *ngIf="loading" class="flex justify-center items-center h-64">
        <i class="pi pi-spin pi-spinner text-4xl text-blue-600"></i>
      </div>

      <div *ngIf="!loading && detail" class="max-w-5xl mx-auto px-4 py-8">
        
        <!-- Header -->
        <div class="mb-6">
          <button (click)="goBack()" class="text-sm text-slate-500 hover:text-blue-600 mb-4 flex items-center gap-1">
            <i class="pi pi-arrow-left"></i> Quay lại
          </button>
          <div class="flex justify-between items-start">
            <div>
              <div class="flex gap-2 items-center mb-2">
                <p-tag [value]="detail.phanLoai" severity="info"></p-tag>
                <p-tag [value]="detail.mucDoMat" [severity]="detail.mucDoMat === 'TOI_MAT' ? 'danger' : 'warn'"></p-tag>
              </div>
              <h1 class="text-3xl font-bold text-slate-800">{{ detail.tieuDe }}</h1>
              <p class="text-slate-500 font-mono mt-2">Mã HS: {{ detail.maHoSo }}</p>
            </div>
          </div>
        </div>

        <!-- content tab -->
        <p-tabs value="0">
            <p-tablist>
                <p-tab value="0"><i class="pi pi-info-circle mr-2"></i> Thông tin chung</p-tab>
                <p-tab value="1" *ngIf="canViewLogs()"><i class="pi pi-history mr-2"></i> Lịch sử thay đổi (Audit)</p-tab>
                <p-tab value="2"><i class="pi pi-file mr-2"></i> File Đính kèm ({{ files.length }})</p-tab>
                <p-tab value="3" *ngIf="canViewLogs()"><i class="pi pi-eye mr-2"></i> Access Log</p-tab>
            </p-tablist>

            <p-tabpanels>
                <p-tabpanel value="0">
                  <div class="bg-white p-6 rounded-xl border border-slate-200 grid grid-cols-2 gap-y-6 gap-x-12">
                    <div>
                      <h3 class="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-1">Đối tượng theo dõi</h3>
                      <p class="text-lg font-medium text-slate-800">{{ detail.doiTuongHoTen }}</p>
                      <p class="text-sm text-slate-500">{{ detail.donViDoiTuong }}</p>
                    </div>
                    <div>
                      <h3 class="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-1">Cán bộ phụ trách</h3>
                      <p class="text-lg font-medium text-slate-800">{{ detail.cbctPhuTrachName || 'Chưa phân công' }}</p>
                    </div>
                    <div>
                      <h3 class="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-1">Đơn vị Quản lý</h3>
                      <p class="text-lg font-medium text-slate-800">{{ detail.tenDonVi || detail.donViId }}</p>
                    </div>
                    <div>
                      <h3 class="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-1">Thời gian ghi nhận</h3>
                      <p class="text-slate-800">{{ detail.ngayMoHoSo | date:'dd/MM/yyyy' }}</p>
                    </div>
                    <div>
                      <h3 class="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-1">Trạng thái</h3>
                      <p-tag [value]="detail.trangThai" severity="info"></p-tag>
                    </div>
                    
                    <div class="col-span-2 mt-4 pt-6 border-t border-slate-100">
                      <h3 class="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-3">Nội dung chi tiết</h3>
                      
                      <!-- AN TOÀN XSS BADGE: Đã nhúng DOMPurify qua SafeHtmlPipe -->
                      <div class="prose max-w-none text-slate-700 editor-content" 
                           [innerHTML]="detail.noiDung | safeHtml">
                      </div>
                    </div>
                  </div>
                </p-tabpanel>
                
                <p-tabpanel value="1" *ngIf="canViewLogs()">
                  <!-- Lịch sử Audit -->
                  <div class="bg-white p-6 rounded-xl border border-slate-200 min-h-[300px]">
                    <p-timeline [value]="auditLogs">
                      <ng-template pTemplate="content" let-event>
                        <div class="mb-6">
                            <span class="text-sm font-medium text-slate-500">{{ event.changedAt | date:'dd/MM/yyyy HH:mm' }}</span>
                            <div class="bg-slate-50 p-4 mt-2 rounded-lg border border-slate-100">
                                <p class="font-medium text-slate-700 mb-1">Trường dữ liệu: <span class="text-blue-600 font-mono">{{ event.fieldName }}</span></p>
                                <div class="grid grid-cols-2 gap-4 text-sm mt-2">
                                  <div class="w-full">
                                    <span class="text-xs text-slate-400 uppercase line-through">Cũ</span>
                                    <div class="bg-red-50 text-red-700 py-1 px-2 rounded mt-1">{{ event.oldValue || '(Empty)' }}</div>
                                  </div>
                                  <div class="w-full">
                                     <span class="text-xs text-slate-400 uppercase">Mới</span>
                                    <div class="bg-green-50 text-green-700 py-1 px-2 rounded mt-1">{{ event.newValue }}</div>
                                  </div>
                                </div>
                            </div>
                        </div>
                      </ng-template>
                    </p-timeline>
                    <div *ngIf="auditLogs.length === 0" class="text-center text-slate-400 mt-10">
                      Chưa có lịch sử chỉnh sửa.
                    </div>
                  </div>
                </p-tabpanel>

                <p-tabpanel value="2">
                   <div class="bg-white p-6 rounded-xl border border-slate-200">
                     <app-file-upload 
                       [url]="uploadUrl" 
                       (onUploadComplete)="fetchFiles()">
                     </app-file-upload>

                     <div class="mt-8 border-t border-slate-100 pt-6">
                       <h3 class="text-lg font-bold text-slate-700 mb-4">Các tệp đã tải lên</h3>
                       <p-table [value]="files" styleClass="p-datatable-sm">
                          <ng-template pTemplate="header">
                              <tr>
                                  <th>Tên File</th>
                                  <th>Dung lượng</th>
                                  <th>Ngày tải</th>
                                  <th style="width: 100px; text-align: center">Tải về</th>
                              </tr>
                          </ng-template>
                          <ng-template pTemplate="body" let-file>
                              <tr>
                                  <td class="font-medium text-blue-600">{{ file.fileName }}</td>
                                  <td>{{ file.fileSize }} Bytes</td>
                                  <td>{{ file.createdAt | date:'short' }}</td>
                                  <td class="text-center">
                                      <p-button icon="pi pi-download" variant="text" size="small" [rounded]="true" (onClick)="downloadFile(file.id, file.fileName)"></p-button>
                                  </td>
                              </tr>
                          </ng-template>
                          <ng-template pTemplate="emptymessage">
                              <tr>
                                  <td colspan="4" class="text-center p-4">Hồ sơ chưa có tài liệu đính kèm.</td>
                              </tr>
                          </ng-template>
                       </p-table>
                     </div>
                   </div>
                </p-tabpanel>

                <p-tabpanel value="3" *ngIf="canViewLogs()">
                  <div class="bg-white p-6 rounded-xl border border-slate-200">
                     <p-table [value]="accessLogs" [paginator]="true" [rows]="10" styleClass="p-datatable-sm">
                        <ng-template pTemplate="header">
                            <tr>
                                <th>Thời gian</th>
                                <th>Người dùng</th>
                                <th>Hành động</th>
                                <th>IP Address</th>
                            </tr>
                        </ng-template>
                        <ng-template pTemplate="body" let-log>
                            <tr>
                                <td>{{ log.accessedAt | date:'MM/dd/yyyy HH:mm:ss' }}</td>
                                <td class="text-sm">
                                    <div class="flex flex-col">
                                        <span class="font-semibold text-slate-800">{{ log.userName || 'Unknown' }}</span>
                                        <span class="text-xs text-slate-400 font-mono">ID: {{ log.userId }}</span>
                                    </div>
                                </td>
                                <td><p-tag [value]="log.action" [severity]="log.action === 'EXPORT' ? 'warn' : 'info'"></p-tag></td>
                                <td class="font-mono text-slate-500">{{ log.ipAddress }}</td>
                            </tr>
                        </ng-template>
                     </p-table>
                  </div>
                </p-tabpanel>
            </p-tabpanels>
        </p-tabs>

      </div>
    </div>
  `,
  styles: [`
    /* Tinh chỉnh render html an toàn */
    .editor-content p { margin-bottom: 0.5rem; }
    .editor-content ul { list-style-type: disc; margin-left: 1.5rem; }
    .editor-content strong { font-weight: 700; color: #1e293b; }
  `]
})
export class ChiTietDieuTraComponent implements OnInit {
  id!: number;
  detail: any;
  loading = true;
  
  auditLogs: any[] = [];
  accessLogs: any[] = [];
  files: any[] = [];
  
  uploadUrl = '';

  constructor(
    private service: HoSoDieuTraService, 
    private route: ActivatedRoute,
    private location: Location,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.uploadUrl = `/api/ho-so-dieu-tra/${this.id}/files`;
    this.fetchDetail();
  }

  fetchDetail() {
    this.loading = true;
    this.service.getById(this.id).subscribe({
      next: (res) => {
        this.detail = res;
        this.loading = false;
        
        // Parallel data fetch
        this.fetchAuditLogs();
        this.fetchFiles();
        if(this.canViewLogs()) {
          this.fetchAccessLogs();
        }
      },
      error: () => this.loading = false
    });
  }

  fetchAuditLogs() {
    this.service.getAuditLogs(this.id).subscribe(res => this.auditLogs = res || []);
  }

  fetchAccessLogs() {
    this.service.getAccessLogs(this.id).subscribe(res => this.accessLogs = res || []);
  }

  fetchFiles() {
    this.service.getFiles(this.id).subscribe(res => this.files = res || []);
  }

  downloadFile(fileId: number, fileName: string) {
    this.service.downloadFile(this.id, fileId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
      
      // Reload access logs to show DOWNLOAD_FILE action
      if (this.canViewLogs()) {
        this.fetchAccessLogs();
      }
    });
  }

  goBack() {
    this.location.back();
  }

  canViewLogs(): boolean {
    return this.authService.hasRole('TRUONG_PHONG') || this.authService.hasRole('CBCT');
  }
}
