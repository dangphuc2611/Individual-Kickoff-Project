import { Component } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ThongTinHinhSuService } from '../../../core/services/thong-tin-hinh-su.service';
import { ButtonModule } from 'primeng/button';
import { FileUploadModule } from 'primeng/fileupload';
import { TabsModule } from 'primeng/tabs';
import { TableModule } from 'primeng/table';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-import-hinh-su',
  standalone: true,
  imports: [CommonModule, ButtonModule, FileUploadModule, TabsModule, TableModule, ToastModule],
  providers: [MessageService],
  template: `
    <div class="bg-slate-50 min-h-screen pb-12">
      <p-toast></p-toast>
      <div class="max-w-5xl mx-auto px-4 py-8">
        <div class="mb-6 flex items-center justify-between">
            <div>
              <button (click)="goBack()" class="text-sm text-slate-500 hover:text-blue-600 mb-2 flex items-center gap-1">
                <i class="pi pi-arrow-left"></i> Quay lại
              </button>
              <h1 class="text-2xl font-bold text-slate-800">Nhập dữ liệu Hình sự (Excel)</h1>
            </div>
            
            <p-button *ngIf="validRows.length > 0" label="Xác nhận Import" icon="pi pi-check" severity="success" (onClick)="confirmImport()" [loading]="saving"></p-button>
        </div>

        <!-- BƯỚC 1 -->
        <div class="bg-white p-8 rounded-xl border border-slate-200 shadow-sm text-center mb-6" *ngIf="!previewMode">
            <i class="pi pi-cloud-upload text-5xl text-blue-500 mb-4"></i>
            <h3 class="text-lg font-medium text-slate-700 mb-2">Tải lên File Excel</h3>
            <p-fileUpload mode="basic" chooseLabel="Chọn File" accept=".xls,.xlsx" maxFileSize="10000000" (onSelect)="onFileSelect($event)" [auto]="true"></p-fileUpload>
        </div>

        <div *ngIf="loading" class="text-center py-10">
            <i class="pi pi-spin pi-spinner text-4xl text-blue-600"></i>
        </div>

        <!-- BƯỚC 2 -->
        <div class="bg-white rounded-xl border border-slate-200 shadow-sm" *ngIf="previewMode && !loading">
            <p-tabs value="0">
                <p-tablist>
                    <p-tab value="0"><i class="pi pi-check-circle mr-2 text-green-600"></i> Hợp lệ ({{ validRows.length }})</p-tab>
                    <p-tab value="1"><i class="pi pi-exclamation-triangle mr-2 text-red-500"></i> Lỗi ({{ errorRows.length }})</p-tab>
                </p-tablist>

                <p-tabpanels>
                  <p-tabpanel value="0">
                      <p-table [value]="validRows" [paginator]="true" [rows]="10" styleClass="p-datatable-sm p-datatable-striped">
                          <ng-template pTemplate="header">
                              <tr>
                                  <th>Mã HS</th>
                                  <th>Tiêu đề</th>
                                  <th>Tội danh</th>
                                  <th>Đối tượng</th>
                                  <th>Kết quả</th>
                              </tr>
                          </ng-template>
                          <ng-template pTemplate="body" let-row>
                              <tr>
                                  <td class="font-medium text-slate-700">{{ row.rowData.maHoSo }}</td>
                                  <td>{{ row.rowData.tieuDe }}</td>
                                  <td>{{ row.rowData.loaiToiDanh }}</td>
                                  <td>{{ row.rowData.doiTuongLienQuan }}</td>
                                  <td>{{ row.rowData.ketQuaXuLy }}</td>
                              </tr>
                          </ng-template>
                      </p-table>
                  </p-tabpanel>
                  
                  <p-tabpanel value="1">
                      <p-table [value]="errorRows" [paginator]="true" [rows]="10" styleClass="p-datatable-sm border-t border-slate-200">
                          <ng-template pTemplate="header">
                              <tr>
                                  <th>#</th>
                                  <th>Lỗi Chi Tiết</th>
                              </tr>
                          </ng-template>
                          <ng-template pTemplate="body" let-row>
                              <tr>
                                  <td class="font-mono text-red-400 font-bold bg-red-50/50">{{ row.rowIndex }}</td>
                                  <td class="text-red-600 bg-red-50/50">
                                      <ul class="list-disc ml-4 text-sm">
                                          <li *ngFor="let err of row.errors">{{ err }}</li>
                                      </ul>
                                  </td>
                              </tr>
                          </ng-template>
                      </p-table>
                  </p-tabpanel>
                </p-tabpanels>
            </p-tabs>
        </div>

      </div>
    </div>
  `
})
export class ImportHinhSuComponent {
  previewMode = false;
  loading = false;
  saving = false;
  
  validRows: any[] = [];
  errorRows: any[] = [];
  
  constructor(
    private service: ThongTinHinhSuService,
    private location: Location,
    private messageService: MessageService
  ) {}

  goBack() { this.location.back(); }

  onFileSelect(event: any) {
    const file = event.files[0];
    if (file) {
      this.loading = true;
      this.previewMode = false;
      this.service.validateImport(file).subscribe({
        next: (res) => {
          this.validRows = res.validRows || [];
          this.errorRows = res.errorRows || [];
          this.loading = false;
          this.previewMode = true;
        },
        error: (err) => {
          this.loading = false;
          this.messageService.add({severity:'error', summary:'Lỗi', detail: 'Không thể xử lý file.'});
        }
      });
    }
  }

  confirmImport() {
    if (this.validRows.length === 0) return;
    this.saving = true;
    const payload = this.validRows.map(r => r.rowData);
    this.service.confirmImport(payload).subscribe({
      next: () => {
        this.saving = false;
        this.messageService.add({severity:'success', summary:'Thành công', detail: 'Đã nhập dữ liệu!'});
        setTimeout(() => this.goBack(), 1500);
      },
      error: () => this.saving = false
    });
  }
}
