import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule, TableLazyLoadEvent } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { HoSoAccessLogService } from '../../core/services/ho-so-access-log.service';

@Component({
  selector: 'app-access-log',
  standalone: true,
  imports: [CommonModule, TableModule, TagModule],
  template: `
    <div class="px-6 py-8">
        <div class="mb-6">
            <h1 class="text-3xl font-bold text-slate-800">Lịch sử Truy cập Hệ thống</h1>
            <p class="text-slate-500 mt-2">Theo dõi toàn bộ các hoạt động truy cập, tải file, xuất file của người dùng.</p>
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
                        <th style="min-width: 180px">Thời gian</th>
                        <th>User ID</th>
                        <th>Hành động</th>
                        <th>Loại HS</th>
                        <th>Mã Record</th>
                        <th>IP Address</th>
                        <th>User Agent</th>
                    </tr>
                </ng-template>
                <ng-template pTemplate="body" let-log>
                    <tr>
                        <td>{{ log.accessedAt | date:'MM/dd/yyyy HH:mm:ss' }}</td>
                        <td class="font-mono text-blue-600 font-medium">{{ log.userId }}</td>
                        <td>
                            <p-tag 
                                [value]="log.action" 
                                [severity]="getSeverity(log.action)">
                            </p-tag>
                        </td>
                        <td><p-tag [value]="log.hoSoType" severity="secondary"></p-tag></td>
                        <td class="font-mono">{{ log.hoSoId || 'N/A' }}</td>
                        <td class="font-mono text-slate-500 text-sm">{{ log.ipAddress }}</td>
                        <td class="text-xs text-slate-400 max-w-xs truncate" [title]="log.userAgent">{{ log.userAgent }}</td>
                    </tr>
                </ng-template>
                <ng-template pTemplate="emptymessage">
                    <tr>
                        <td colspan="7" class="text-center p-4">Không có dữ liệu lịch sử truy cập.</td>
                    </tr>
                </ng-template>
            </p-table>
        </div>
    </div>
  `
})
export class AccessLogComponent implements OnInit {
  logs: any[] = [];
  totalRecords: number = 0;
  loading: boolean = true;

  constructor(
    private accessLogService: HoSoAccessLogService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loading = true;
  }

  loadLogs(event: TableLazyLoadEvent) {
    this.loading = true;
    const page = event.first ? event.first / (event.rows || 10) : 0;
    const size = event.rows || 10;
    
    this.accessLogService.getAllLogs(page, size).subscribe({
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

  getSeverity(action: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' | undefined {
    switch(action) {
      case 'VIEW_DETAIL': return 'info';
      case 'VIEW_LIST': return 'secondary';
      case 'DOWNLOAD_FILE': return 'success';
      case 'EXPORT': return 'warn';
      default: return 'info';
    }
  }
}
