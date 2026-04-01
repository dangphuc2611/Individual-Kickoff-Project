import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ColumnDef, HoSoDataTableComponent } from '../../../shared/components/ho-so-data-table/ho-so-data-table.component';
import { HoSoDieuTraService } from '../../../core/services/ho-so-dieu-tra.service';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-danh-sach-dieu-tra',
  standalone: true,
  imports: [CommonModule, HoSoDataTableComponent, ButtonModule, SelectModule, InputTextModule, DatePickerModule, FormsModule],
  template: `
    <div class="bg-slate-50 min-h-screen">
      <div class="max-w-7xl mx-auto px-4 py-8">
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <div>
            <h1 class="text-2xl font-bold text-slate-800">Hồ sơ Điều tra Cơ bản</h1>
            <p class="text-slate-500 text-sm mt-1">Danh sách hồ sơ điều tra và theo dõi đối tượng</p>
          </div>
          <div class="flex gap-2">
            <p-button *ngIf="authService.canCreate()" label="Thêm mới" icon="pi pi-plus" (onClick)="goToCreate()" severity="primary"></p-button>
            <p-button label="Xuất Excel" icon="pi pi-file-excel" (onClick)="exportExcel()" severity="success"></p-button>
            <p-button *ngIf="authService.canCreate()" label="Nhập Excel" icon="pi pi-upload" (onClick)="goToImport()" severity="secondary" variant="outlined"></p-button>
          </div>
        </div>

        <!-- Filter Bar -->
        <div class="bg-white rounded-xl border border-slate-200 shadow-sm px-5 py-4 mb-5 flex flex-wrap gap-4 items-end">
          <div class="flex flex-col gap-1 min-w-[250px]">
            <label class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Tìm kiếm</label>
            <input pInputText type="text" [(ngModel)]="filters.search" placeholder="Mã hồ sơ, tiêu đề..." class="w-full" />
          </div>

          <div class="flex flex-col gap-1 min-w-[200px]">
            <label class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Mức độ mật</label>
            <p-select [options]="mucDoMatOptions" [(ngModel)]="filters.mucDoMat" placeholder="Tất cả" [style]="{'width':'100%'}"></p-select>
          </div>

          <div class="flex flex-col gap-1 min-w-[200px]">
            <label class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Trạng thái</label>
            <p-select [options]="trangThaiOptions" [(ngModel)]="filters.trangThai" placeholder="Tất cả" [style]="{'width':'100%'}"></p-select>
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

        <!-- Bảng Dữ liệu chung -->
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
export class DanhSachDieuTraComponent implements OnInit {
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
    { field: 'phanLoai', header: 'Phân loại', type: 'badge' },
    { field: 'mucDoMat', header: 'Độ Mật', type: 'badge' },
    { field: 'ngayMoHoSo', header: 'Ngày mở', type: 'date' },
    { field: 'trangThai', header: 'Trạng thái', type: 'badge' },
    { field: 'id', header: 'Thao tác', type: 'action' }
  ];

  mucDoMatOptions = [
    { label: 'Tất cả', value: null },
    { label: 'Tối Mật', value: 'TOI_MAT' },
    { label: 'Mật', value: 'MAT' },
    { label: 'Thông Thường', value: 'THUONG' }
  ];

  trangThaiOptions = [
    { label: 'Tất cả', value: null },
    { label: 'Đang theo dõi', value: 'DANG_THEO_DOI' },
    { label: 'Tạm dừng', value: 'TAM_DUNG' },
    { label: 'Đã kết thúc', value: 'KET_THUC' }
  ];

  constructor(
    private service: HoSoDieuTraService, 
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

  goToDetail(id: number) { this.router.navigate(['/dieu-tra', id]); }
  goToEdit(id: number) { this.router.navigate(['/dieu-tra', id, 'edit']); }
  goToCreate() { this.router.navigate(['/dieu-tra/create']); }
  goToImport() { this.router.navigate(['/dieu-tra/import']); }

  confirmDelete(id: number) {
    if(confirm('Bạn có chắc muốn xóa (soft delete) hồ sơ này không?')) {
      this.service.delete(id).subscribe(() => this.fetchData());
    }
  }

  exportExcel() {
    this.service.exportExcel(this.filters).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Danh_Sach_Dieu_Tra_${new Date().getTime()}.xlsx`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
