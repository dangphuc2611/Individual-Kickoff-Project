import { Component, EventEmitter, Input, Output, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';

export interface ColumnDef {
  field: string;
  header: string;
  type?: 'text' | 'date' | 'badge' | 'action';
}

@Component({
  selector: 'app-ho-so-data-table',
  standalone: true,
  imports: [CommonModule, TableModule, ButtonModule, TagModule],
  template: `
    <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
      <p-table 
        [value]="data" 
        [loading]="loading" 
        [paginator]="true" 
        [rows]="10" 
        [totalRecords]="totalRecords"
        [lazy]="true"
        (onLazyLoad)="onLazyLoad.emit($event)"
        [showCurrentPageReport]="true" 
        [rowsPerPageOptions]="[10, 25, 50]" 
        currentPageReportTemplate="Hiển thị {first} đến {last} của {totalRecords} kết quả" 
        styleClass="p-datatable-striped p-datatable-sm"
        dataKey="id">
        
        <ng-template pTemplate="header">
          <tr>
            <th *ngFor="let col of columns" [style.width]="col.type === 'action' ? '10%' : 'auto'" [style.text-align]="col.type === 'action' ? 'center' : 'left'">
              {{ col.header }}
            </th>
          </tr>
        </ng-template>

        <ng-template pTemplate="body" let-rowData>
          <tr>
            <td *ngFor="let col of columns" [ngClass]="{'text-center': col.type === 'action'}">
              
              <!-- Cột Text thông thường -->
              <ng-container *ngIf="!col.type || col.type === 'text'">
                {{ rowData[col.field] }}
              </ng-container>

              <!-- Cột Ngày tháng -->
              <ng-container *ngIf="col.type === 'date'">
                {{ rowData[col.field] | date:'dd/MM/yyyy' }}
              </ng-container>

              <!-- Cột Badge (Trạng thái, Phân loại, Mức độ mật) -->
              <ng-container *ngIf="col.type === 'badge'">
                <p-tag [value]="formatBadge(rowData[col.field])" [severity]="getBadgeSeverity(col.field, rowData[col.field])"></p-tag>
              </ng-container>

              <!-- Cột Thao tác -->
              <ng-container *ngIf="col.type === 'action'">
                <div class="flex items-center justify-center gap-1">
                  <p-button icon="pi pi-eye" (onClick)="onView.emit(rowData)" variant="text" severity="info" size="small" [rounded]="true" title="Xem chi tiết"></p-button>
                  <p-button icon="pi pi-pencil" (onClick)="onEdit.emit(rowData)" variant="text" severity="warn" size="small" [rounded]="true" title="Chỉnh sửa"></p-button>
                  <p-button icon="pi pi-trash" (onClick)="onDelete.emit(rowData)" variant="text" severity="danger" size="small" [rounded]="true" title="Xóa"></p-button>
                </div>
              </ng-container>

            </td>
          </tr>
        </ng-template>

        <ng-template pTemplate="emptymessage">
          <tr>
            <td [attr.colspan]="columns.length" class="text-center p-8 text-slate-500">
              <i class="pi pi-inbox text-4xl mb-3 text-slate-300"></i>
              <p>Không tìm thấy dữ liệu hồ sơ phù hợp.</p>
            </td>
          </tr>
        </ng-template>
        
      </p-table>
    </div>
  `
})
export class HoSoDataTableComponent {
  @Input() columns: ColumnDef[] = [];
  @Input() data: any[] = [];
  @Input() totalRecords: number = 0;
  @Input() loading: boolean = false;

  @Output() onLazyLoad = new EventEmitter<any>();
  @Output() onView = new EventEmitter<any>();
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();

  // Xử lý hiển thị Label của Badge (Xóa dấu _, viết hoa chữ cái đầu)
  formatBadge(value: string): string {
    if (!value) return '';
    const cleanStr = value.replace(/_/g, ' ');
    return cleanStr.charAt(0).toUpperCase() + cleanStr.slice(1).toLowerCase();
  }

  // Định tuyến màu sắc cho Badge dựa trên Field Type và Value
  getBadgeSeverity(field: string, value: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    if (!value) return 'secondary';
    
    // Mức độ mật
    if (field === 'mucDoMat') {
      if (value === 'TOI_MAT') return 'danger';
      if (value === 'MAT') return 'warn';
      return 'info';
    }

    // Trạng thái Hồ sơ
    if (field === 'trangThai' || field === 'ketQuaXuLy') {
      if (['DANG_THEO_DOI', 'DANG_XU_LY', 'DANG_DIEU_TRA'].includes(value)) return 'info';
      if (['KET_THUC', 'DA_XU_LY'].includes(value)) return 'success';
      if (['TAM_DUNG'].includes(value)) return 'warn';
    }
    
    return 'secondary';
  }
}
