import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ColumnDef, HoSoDataTableComponent } from '../../../shared/components/ho-so-data-table/ho-so-data-table.component';
import { ThongTinHinhSuService } from '../../../core/services/thong-tin-hinh-su.service';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DatePickerModule } from 'primeng/datepicker';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-danh-sach-hinh-su',
  standalone: true,
  imports: [CommonModule, HoSoDataTableComponent, ButtonModule, SelectModule, InputTextModule, DatePickerModule, FormsModule],
  template: `
    <div class="bg-slate-50 min-h-screen">
      <div class="max-w-7xl mx-auto px-4 py-8">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h1 class="text-2xl font-bold text-slate-800">Thông tin Hình sự</h1>
            <p class="text-slate-500 text-sm mt-1">Danh sách thông tin tội phạm, vi phạm pháp luật</p>
          </div>
          <div class="flex gap-2">
            <p-button label="Thêm mới" icon="pi pi-plus" (onClick)="goToCreate()" severity="primary"></p-button>
            <p-button label="Xuất Excel" icon="pi pi-file-excel" (onClick)="exportExcel()" severity="success"></p-button>
            <p-button label="Nhập Excel" icon="pi pi-upload" (onClick)="goToImport()" severity="secondary" variant="outlined"></p-button>
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
export class DanhSachHinhSuComponent implements OnInit {
  data: any[] = [];
  totalRecords = 0;
  loading = false;
  
  currentPage = 0;
  pageSize = 10;
  filters: any = { search: '', mucDoMat: null, ketQuaXuLy: null };
  dateRange: Date[] | null = null;

  tableColumns: ColumnDef[] = [
    { field: 'maThongTin', header: 'Mã hồ sơ', type: 'text' },
    { field: 'tieuDe', header: 'Tiêu đề', type: 'text' },
    { field: 'loaiToiDanh', header: 'Tội danh', type: 'badge' },
    { field: 'mucDoMat', header: 'Mức độ mật', type: 'badge' },
    { field: 'ngayXayRa', header: 'Ngày xảy ra', type: 'date' },
    { field: 'ketQuaXuLy', header: 'Kết quả', type: 'badge' },
    { field: 'id', header: 'Thao tác', type: 'action' }
  ];

  constructor(private service: ThongTinHinhSuService, private router: Router, private cdr: ChangeDetectorRef) {}

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
    this.filters = { search: '', mucDoMat: null, ketQuaXuLy: null };
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

  goToDetail(id: number) { this.router.navigate(['/hinh-su', id]); }
  goToEdit(id: number) { this.router.navigate(['/hinh-su', id, 'edit']); }
  goToCreate() { this.router.navigate(['/hinh-su/create']); }
  goToImport() { this.router.navigate(['/hinh-su/import']); }

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
      a.download = `DanhSachHinhSu.xlsx`;
      a.click();
    });
  }
}
