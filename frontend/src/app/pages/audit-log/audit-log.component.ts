import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { HoSoAuditLogService } from '../../core/services/ho-so-audit-log.service';

@Component({
  selector: 'app-audit-log',
  standalone: true,
  imports: [CommonModule, TableModule, TagModule],
  template: `
    <div class="px-6 py-8">
        <div class="mb-6">
            <h1 class="text-3xl font-bold text-slate-800">Lịch sử Thay đổi (Audit Log)</h1>
            <p class="text-slate-500 mt-2">Theo dõi các thay đổi dữ liệu trên toàn hệ thống (Thêm mới, Chỉnh sửa, Xóa).</p>
        </div>

        <div class="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
            <p-table 
                [value]="logs" 
                [lazy]="true" 
                (onLazyLoad)="loadLogs($event)" 
                [paginator]="true" 
                [rows]="10" 
                [totalRecords]="totalRecords" 
                [loading]="loading"
                styleClass="p-datatable-sm"
                [rowsPerPageOptions]="[10, 20, 50]">
                
                <ng-template pTemplate="header">
                    <tr>
                        <th style="min-width: 170px">Thời gian</th>
                        <th style="min-width: 150px">Người thay đổi</th>
                        <th>Hành động</th>
                        <th>Loại HS</th>
                        <th>Trường</th>
                        <th style="max-width: 200px">Giá trị cũ</th>
                        <th style="max-width: 200px">Giá trị mới</th>
                    </tr>
                </ng-template>
                <ng-template pTemplate="body" let-log>
                    <tr>
                        <td class="text-slate-600">{{ log.changedAt | date:'MM/dd/yyyy HH:mm:ss' }}</td>
                        <td>
                            <div class="flex flex-col">
                                <span class="font-semibold text-slate-800">{{ log.changedByName || 'Unknown' }}</span>
                                <span class="text-xs text-slate-400 font-mono">ID: {{ log.changedById }}</span>
                            </div>
                        </td>
                        <td>
                            <p-tag 
                                [value]="log.action" 
                                [severity]="getActionSeverity(log.action)">
                            </p-tag>
                        </td>
                        <td><p-tag [value]="log.hoSoType" severity="secondary"></p-tag></td>
                        <td class="font-mono text-blue-600 text-sm">{{ log.fieldName }}</td>
                        <td class="max-w-xs truncate text-xs text-red-500 bg-red-50 p-1 rounded" [title]="log.oldValue || '(Trống)'">
                            {{ log.oldValue || '(Trống)' }}
                        </td>
                        <td class="max-w-xs truncate text-xs text-green-600 bg-green-50 p-1 rounded" [title]="log.newValue">
                            {{ log.newValue }}
                        </td>
                    </tr>
                </ng-template>
                <ng-template pTemplate="emptymessage">
                    <tr>
                        <td colspan="7" class="text-center p-4">Không có dữ liệu lịch sử thay đổi.</td>
                    </tr>
                </ng-template>
            </p-table>
        </div>
    </div>
  `
})
export class AuditLogComponent implements OnInit {
  logs: any[] = [];
  totalRecords: number = 0;
  loading: boolean = true;

  constructor(
    private auditLogService: HoSoAuditLogService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loading = true;
  }

  loadLogs(event: TableLazyLoadEvent) {
    this.loading = true;
    const page = event.first ? event.first / (event.rows || 10) : 0;
    const size = event.rows || 10;
    
    this.auditLogService.getAllLogs(page, size).subscribe({
      next: (res) => {
        this.logs = res.content;
        this.totalRecords = res.totalElements;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getActionSeverity(action: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' | undefined {
    switch(action) {
      case 'CREATE': return 'success';
      case 'UPDATE': return 'info';
      case 'DELETE': return 'danger';
      case 'IMPORT': return 'warn';
      default: return 'info';
    }
  }
}
