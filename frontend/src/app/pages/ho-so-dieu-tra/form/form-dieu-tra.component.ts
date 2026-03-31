import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HoSoDieuTraService } from '../../../core/services/ho-so-dieu-tra.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { EditorModule } from 'primeng/editor';
import { DatePickerModule } from 'primeng/datepicker';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { DonViResponse, DonViService } from '../../../core/services/don-vi.service';

@Component({
  selector: 'app-form-dieu-tra',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ButtonModule, InputTextModule, SelectModule, EditorModule, DatePickerModule, ToastModule],
  providers: [MessageService],
  template: `
    <div class="bg-slate-50 min-h-screen pb-12">
      <p-toast></p-toast>
      <div class="max-w-4xl mx-auto px-4 py-8">
        
        <div class="mb-6 flex items-center justify-between">
            <div>
              <button (click)="goBack()" class="text-sm text-slate-500 hover:text-blue-600 mb-2 flex items-center gap-1">
                <i class="pi pi-arrow-left"></i> Quay lại
              </button>
              <h1 class="text-2xl font-bold text-slate-800">{{ isEdit ? 'Cập nhật Hồ sơ Điều tra' : 'Tạo mới Hồ sơ Điều tra' }}</h1>
            </div>
        </div>

        <div class="bg-white p-6 md:p-8 rounded-xl border border-slate-200 shadow-sm">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Tiêu đề (Bắt buộc) -->
                <div class="col-span-1 md:col-span-2 flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Tiêu đề Hồ sơ <span class="text-red-500">*</span></label>
                    <input pInputText formControlName="tieuDe" class="w-full" [ngClass]="{'ng-invalid ng-dirty': submitted && f['tieuDe'].errors}" />
                    <small *ngIf="submitted && f['tieuDe'].errors?.['required']" class="text-red-500">Tiêu đề không được để trống.</small>
                </div>

                <!-- Đối tượng Name -->
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Họ tên Đối tượng <span class="text-red-500">*</span></label>
                    <input pInputText formControlName="doiTuongHoTen" class="w-full" [ngClass]="{'ng-invalid ng-dirty': submitted && f['doiTuongHoTen'].errors}" />
                </div>
                
                <!-- Đơn vị đối tượng -->
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Đơn vị Đối tượng</label>
                    <input pInputText formControlName="donViDoiTuong" class="w-full" />
                </div>

                <!-- Phân loại -->
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Phân loại <span class="text-red-500">*</span></label>
                    <p-select [options]="phanLoaiOptions" formControlName="phanLoai" [style]="{'width':'100%'}" appendTo="body" [ngClass]="{'ng-invalid ng-dirty': submitted && f['phanLoai'].errors}"></p-select>
                </div>

                <!-- Mức độ Mật -->
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Mức độ mật <span class="text-red-500">*</span></label>
                    <p-select [options]="mucDoMatOptions" formControlName="mucDoMat" [style]="{'width':'100%'}" appendTo="body" [ngClass]="{'ng-invalid ng-dirty': submitted && f['mucDoMat'].errors}"></p-select>
                </div>

                <!-- Ngày mở -->
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Ngày lấy/Ghi nhận <span class="text-red-500">*</span></label>
                    <p-datepicker formControlName="ngayMoHoSo" dateFormat="yy-mm-dd" appendTo="body" [style]="{'width':'100%'}" [ngClass]="{'ng-invalid ng-dirty': submitted && f['ngayMoHoSo'].errors}"></p-datepicker>
                </div>

                <!-- Trạng thái -->
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Trạng thái <span class="text-red-500">*</span></label>
                    <p-select [options]="trangThaiOptions" formControlName="trangThai" [style]="{'width':'100%'}" appendTo="body" [ngClass]="{'ng-invalid ng-dirty': submitted && f['trangThai'].errors}"></p-select>
                </div>

                <!-- Đơn vị quản lý select -->
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Đơn vị Quản lý <span class="text-red-500">*</span></label>
                    <p-select [options]="donViOptions" formControlName="donViId" optionLabel="label" optionValue="value" [filter]="true" filterBy="label" [style]="{'width':'100%'}" appendTo="body" placeholder="Chọn đơn vị..."></p-select>
                </div>
            </div>

            <!-- Ghi chú -->
            <div class="flex flex-col gap-2 mt-4">
                <label class="text-sm font-semibold text-slate-700">Ghi chú</label>
                <input pInputText formControlName="ghiChu" class="w-full" placeholder="Nhập ghi chú thêm nếu có..." />
            </div>

            <!-- NỘI DUNG (Rich Text Editor) -->
            <div class="flex flex-col gap-2 border-t border-slate-100 pt-6 mt-6">
                <label class="text-sm font-semibold text-slate-700">Nội dung chi tiết <span class="text-red-500">*</span></label>
                <p-editor formControlName="noiDung" [style]="{'height':'250px'}" placeholder="Nhập nội dung hồ sơ..."></p-editor>
            </div>
            
            <!-- BUTTONS -->
            <div class="flex justify-end gap-3 pt-6 border-t border-slate-100">
                <p-button label="Huỷ bỏ" icon="pi pi-times" severity="secondary" variant="text" (onClick)="goBack()"></p-button>
                <p-button type="submit" label="Lưu Hồ Sơ" icon="pi pi-check" [loading]="saving"></p-button>
            </div>
          </form>
        </div>

      </div>
    </div>
  `
})
export class FormDieuTraComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  id!: number;
  loading = false;
  saving = false;
  submitted = false;
  donViOptions: { label: string, value: number }[] = [];

  phanLoaiOptions = [
    { label: 'Điều tra cơ bản', value: 'DIEU_TRA_CO_BAN' },
    { label: 'Theo dõi', value: 'THEO_DOI' },
    { label: 'Đặc biệt', value: 'DAC_BIET' }
  ];

  mucDoMatOptions = [
    { label: 'Thông thường', value: 'THUONG' },
    { label: 'Mật', value: 'MAT' },
    { label: 'Tối mật', value: 'TOI_MAT' }
  ];

  trangThaiOptions = [
    { label: 'Đang theo dõi', value: 'DANG_THEO_DOI' },
    { label: 'Tạm dừng', value: 'TAM_DUNG' },
    { label: 'Kết thúc', value: 'KET_THUC' }
  ];

  constructor(
    private fb: FormBuilder,
    private service: HoSoDieuTraService,
    private donViService: DonViService,
    private route: ActivatedRoute,
    private router: Router,
    private location: Location,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.fetchUnits();
    this.form = this.fb.group({
      tieuDe: ['', [Validators.required, Validators.maxLength(255)]],
      phanLoai: ['THEO_DOI', Validators.required],
      mucDoMat: ['THUONG', Validators.required],
      doiTuongHoTen: ['', Validators.required],
      donViDoiTuong: ['', Validators.required],
      ngayMoHoSo: [new Date(), Validators.required],
      noiDung: ['', Validators.required],
      trangThai: ['DANG_THEO_DOI', Validators.required],
      ghiChu: ['', [Validators.maxLength(1000)]],
      donViId: [1, Validators.required]
    });

    this.id = Number(this.route.snapshot.paramMap.get('id'));
    if (this.id) {
      this.isEdit = true;
      this.fetchData();
    }
  }

  fetchUnits() {
    this.donViService.getAll().subscribe({
      next: (res) => {
        this.donViOptions = res.map(d => ({ label: d.tenDonVi, value: d.id }));
      },
      error: () => {
        this.messageService.add({severity:'error', summary:'Lỗi', detail: 'Không thể tải danh sách đơn vị'});
      }
    });
  }

  get f() { return this.form.controls; }

  fetchData() {
    this.loading = true;
    this.service.getById(this.id).subscribe({
      next: (res) => {
        // Map data to form
        this.form.patchValue({
          tieuDe: res.tieuDe,
          phanLoai: res.phanLoai,
          mucDoMat: res.mucDoMat,
          doiTuongHoTen: res.doiTuongHoTen,
          donViDoiTuong: res.donViDoiTuong,
          ngayMoHoSo: new Date(res.ngayMoHoSo),
          noiDung: res.noiDung,
          trangThai: res.trangThai,
          ghiChu: res.ghiChu,
          donViId: res.donViId
        });
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.form.invalid) return;

    this.saving = true;
    const payload = this.form.value;
    // Format date again just in case
    if (payload.ngayMoHoSo instanceof Date) {
      const d = payload.ngayMoHoSo;
      payload.ngayMoHoSo = d.toISOString().split('T')[0];
    }

    if (this.isEdit) {
      this.service.update(this.id, payload).subscribe({
        next: () => this.onSuccess(),
        error: (err) => this.onError(err)
      });
    } else {
      this.service.create(payload).subscribe({
        next: () => this.onSuccess(),
        error: (err) => this.onError(err)
      });
    }
  }

  onSuccess() {
    this.saving = false;
    this.messageService.add({severity:'success', summary:'Thành công', detail: 'Đã lưu hồ sơ thành công!'});
    setTimeout(() => this.goBack(), 1000);
  }

  onError(err: any) {
    this.saving = false;
    this.messageService.add({severity:'error', summary:'Lỗi Server', detail: err.error?.message || 'Có lỗi xảy ra khi lưu dữ liệu.'});
  }

  goBack() {
    this.location.back();
  }
}
