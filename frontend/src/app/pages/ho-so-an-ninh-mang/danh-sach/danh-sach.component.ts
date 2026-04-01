import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ColumnDef, HoSoDataTableComponent } from '../../../shared/components/ho-so-data-table/ho-so-data-table.component';
import { HoSoAnNinhMangService } from '../../../core/services/ho-so-an-ninh-mang.service';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-danh-sach-an-ninh-mang',
  standalone: true,
  imports: [CommonModule, HoSoDataTableComponent, ButtonModule, SelectModule, InputTextModule, DatePickerModule, FormsModule],
  template: `
    <div class="bg-slate-50 min-h-screen">
      <div class="max-w-7xl mx-auto px-4 py-8">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h1 class="text-2xl font-bold text-slate-800">Hồ sơ An Ninh Mạng</h1>
            <p class="text-slate-500 text-sm mt-1">Danh sách sự cố và hồ sơ an ninh mạng</p>
          </div>
          <div class="flex gap-2">
            <p-button *ngIf="authService.canCreate()" label="Thêm mới" icon="pi pi-plus" (onClick)="goToCreate()" severity="primary"></p-button>
            <p-button label="Xuất Excel" icon="pi pi-file-excel" (onClick)="exportExcel()" severity="success"></p-button>
            <p-button *ngIf="authService.canCreate()" label="Nhập Excel" icon="pi pi-upload" (onClick)="goToImport()" severity="secondary" variant="outlined"></p-button>
          </div>
        </div>

        <div class="bg-white rounded-xl border border-slate-200 shadow-sm px-5 py-4 mb-5 flex flex-wrap gap-4 items-end">
          <div class="flex flex-col gap-1 min-w-[250px]">
            <label class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Tìm kiếm</label>
            <input pInputText type="text" [(ngModel)]="filters.search" placeholder="Mã hồ sơ, tiêu đề..." class="w-full" />
          </div>

          <div class="flex flex-col gap-1 min-w-[200px]">
            <label class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Khoảng thời gian</label>
            <p-datepicker [(ngModel)]="dateRange" selectionMode="range" [readonlyInput]="true" placeholder="Từ ngày - Đến ngày" dateFormat="dd/mm/yy" [showIcon]="true"></p-datepicker>
          </div>

          <div class="flex gap-2">
            <p-button label="Lọc" icon="pi pi-filter" (onClick)="applyFilter()"></p-button>
            <p-button label="Làm mới" icon="pi pi-refresh" (onClick)="resetFilter()" severity="secondary" variant="outlined"></p-button>
          </div>
        </div>

        <app-ho-so-data-table 
          [columns]="tableColumns"
          [data]="data"
          [totalRecords]="totalRecords"
          [loading]="loading"
          (onLazyLoad)="loadData($event)"
          (onView)="goToDetail($event.id)"
          (onEdit)="goToEdit($event.id)"
          (onDelete)="confirmDelete($event.id)">
        </app-ho-so-data-table>
      </div>
    </div>
  `
})
export class DanhSachAnNinhMangComponent implements OnInit {
  data: any[] = [];
  totalRecords = 0;
  loading = false;
  
  currentPage = 0;
  pageSize = 10;
  filters: any = { search: '', mucDoMat: null, trangThai: null };
  dateRange: Date[] | null = null;

  tableColumns: ColumnDef[] = [
    { field: 'maHoSo', header: 'Mã hồ sơ', type: 'text' },
    { field: 'tieuDe', header: 'Tiêu đề', type: 'text' },
    { field: 'loaiTanCong', header: 'Loại tấn công', type: 'badge' },
    { field: 'mucDoMat', header: 'Mức độ mật', type: 'badge' },
    { field: 'ngayPhatHien', header: 'Phát hiện', type: 'date' },
    { field: 'trangThai', header: 'Trạng thái', type: 'badge' },
    { field: 'id', header: 'Thao tác', type: 'action' }
  ];

  constructor(
    private service: HoSoAnNinhMangService, 
    private router: Router, 
    private cdr: ChangeDetectorRef,
    public authService: AuthService
  ) {}

  ngOnInit() {}

  loadData(event: any) {
    this.currentPage = event.first ? Math.floor(event.first / (event.rows || 10)) : 0;
    this.pageSize = event.rows || 10;
    this.fetchData();
  }

  fetchData() {
    this.loading = true;
    this.cdr.detectChanges();
    this.service.getAll(this.currentPage, this.pageSize, this.filters).subscribe({
      next: (res: any) => {
        this.data = res.content || [];
        this.totalRecords = res.totalElements || 0;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilter() {
    this.currentPage = 0;
    if (this.dateRange && this.dateRange[0]) {
      this.filters.dateFrom = this.formatDate(this.dateRange[0]);
      if (this.dateRange[1]) {
        this.filters.dateTo = this.formatDate(this.dateRange[1]);
      } else {
        this.filters.dateTo = null;
      }
    } else {
      this.filters.dateFrom = null;
      this.filters.dateTo = null;
    }
    this.fetchData();
  }

  resetFilter() {
    this.filters = { search: '', mucDoMat: null, trangThai: null };
    this.dateRange = null;
    this.applyFilter();
  }

  formatDate(date: Date): string {
    const d = new Date(date);
    let month = '' + (d.getMonth() + 1);
    let day = '' + d.getDate();
    const year = d.getFullYear();
    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;
    return [year, month, day].join('-');
  }

  goToDetail(id: number) { this.router.navigate(['/an-ninh-mang', id]); }
  goToEdit(id: number) { this.router.navigate(['/an-ninh-mang', id, 'edit']); }
  goToCreate() { this.router.navigate(['/an-ninh-mang/create']); }
  goToImport() { this.router.navigate(['/an-ninh-mang/import']); }

  confirmDelete(id: number) {
    if(confirm('Chắc chắn xóa (soft delete)?')) {
      this.service.delete(id).subscribe(() => this.fetchData());
    }
  }

  exportExcel() {
    this.service.exportExcel(this.filters).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `DanhSachAnNinhMang.xlsx`;
      a.click();
    });
  }
}
