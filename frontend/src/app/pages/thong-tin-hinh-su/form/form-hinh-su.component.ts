import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ThongTinHinhSuService } from '../../../core/services/thong-tin-hinh-su.service';
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
  selector: 'app-form-hinh-su',
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
              <h1 class="text-2xl font-bold text-slate-800">{{ isEdit ? 'Cập nhật Vụ việc Hình sự' : 'Tạo mới Vụ việc Hình sự' }}</h1>
            </div>
        </div>

        <div class="bg-white p-6 md:p-8 rounded-xl border border-slate-200 shadow-sm">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-6">
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Tiêu đề (Bắt buộc) -->
                <div class="col-span-1 md:col-span-2 flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Tiêu đề vụ việc <span class="text-red-500">*</span></label>
                    <input pInputText formControlName="tieuDe" class="w-full" [ngClass]="{'ng-invalid ng-dirty': submitted && f['tieuDe'].errors}" />
                    <small *ngIf="submitted && f['tieuDe'].errors?.['required']" class="text-red-500">Tiêu đề không được để trống.</small>
                </div>

                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Đối tượng / Nghi can <span class="text-red-500">*</span></label>
                    <input pInputText formControlName="doiTuongLienQuan" class="w-full" [ngClass]="{'ng-invalid ng-dirty': submitted && f['doiTuongLienQuan'].errors}" />
                </div>
                
                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Đơn vị liên quan</label>
                    <input pInputText formControlName="donViLienQuan" class="w-full" />
                </div>

                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Loại tội danh <span class="text-red-500">*</span></label>
                    <p-select [options]="loaiToiDanhOptions" formControlName="loaiToiDanh" [style]="{'width':'100%'}" appendTo="body"></p-select>
                </div>

                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Mức độ mật <span class="text-red-500">*</span></label>
                    <p-select [options]="mucDoMatOptions" formControlName="mucDoMat" [style]="{'width':'100%'}" appendTo="body"></p-select>
                </div>

                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Địa điểm</label>
                    <input pInputText formControlName="diaDiem" class="w-full" />
                </div>

                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Ngày xảy ra <span class="text-red-500">*</span></label>
                    <p-datepicker formControlName="ngayXayRa" dateFormat="yy-mm-dd" appendTo="body" [style]="{'width':'100%'}"></p-datepicker>
                </div>

                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Kết quả xử lý <span class="text-red-500">*</span></label>
                    <p-select [options]="ketQuaXuLyOptions" formControlName="ketQuaXuLy" [style]="{'width':'100%'}" appendTo="body"></p-select>
                </div>

                <div class="flex flex-col gap-2">
                    <label class="text-sm font-semibold text-slate-700">Đơn vị quản lý <span class="text-red-500">*</span></label>
                    <p-select [options]="donViOptions" formControlName="donViId" optionLabel="label" optionValue="value" [filter]="true" filterBy="label" [style]="{'width':'100%'}" appendTo="body" placeholder="Chọn đơn vị..."></p-select>
                </div>
            </div>

            <div class="flex flex-col gap-2 border-t border-slate-100 pt-6 mt-6">
                <label class="text-sm font-semibold text-slate-700">Mô tả diễn biến <span class="text-red-500">*</span></label>
                <p-editor formControlName="moTaDienBien" [style]="{'height':'250px'}" placeholder="Nhập diễn biến vụ việc..."></p-editor>
                <small *ngIf="submitted && f['moTaDienBien'].errors?.['required']" class="text-red-500">Mô tả diễn biến không được để trống.</small>
            </div>
            
            <div class="flex justify-end gap-3 pt-6 border-t border-slate-100">
                <p-button label="Huỷ bỏ" icon="pi pi-times" severity="secondary" variant="text" (onClick)="goBack()"></p-button>
                <p-button type="submit" label="Lưu Vụ Việc" icon="pi pi-check" [loading]="saving"></p-button>
            </div>
          </form>
        </div>

      </div>
    </div>
  `
})
export class FormHinhSuComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  id!: number;
  loading = false;
  saving = false;
  submitted = false;
  donViOptions: { label: string, value: number }[] = [];

  loaiToiDanhOptions = [
    { label: 'Hình sự', value: 'HINH_SU' },
    { label: 'Ma túy', value: 'MA_TUY' },
    { label: 'Tham nhũng', value: 'THAM_NHUNG' },
    { label: 'Khác', value: 'KHAC' }
  ];

  mucDoMatOptions = [
    { label: 'Thường', value: 'THUONG' },
    { label: 'Mật', value: 'MAT' },
    { label: 'Tối mật', value: 'TOI_MAT' }
  ];

  ketQuaXuLyOptions = [
    { label: 'Đang xử lý', value: 'DANG_XU_LY' },
    { label: 'Đã xử lý', value: 'DA_XU_LY' },
    { label: 'Chuyển cơ quan khác', value: 'CHUYEN_CO_QUAN_KHAC' }
  ];

  constructor(
    private fb: FormBuilder,
    private service: ThongTinHinhSuService,
    private donViService: DonViService,
    private route: ActivatedRoute,
    private location: Location,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.fetchUnits();
    this.form = this.fb.group({
      tieuDe: ['', [Validators.required, Validators.maxLength(255)]],
      loaiToiDanh: ['HINH_SU', Validators.required],
      mucDoMat: ['THUONG', Validators.required],
      doiTuongLienQuan: ['', Validators.required],
      donViLienQuan: ['', Validators.required],
      diaDiem: [''],
      ngayXayRa: [new Date(), Validators.required],
      moTaDienBien: ['', Validators.required],
      ketQuaXuLy: ['DANG_XU_LY', Validators.required],
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
        this.form.patchValue({
          tieuDe: res.tieuDe,
          loaiToiDanh: res.loaiToiDanh,
          mucDoMat: res.mucDoMat,
          doiTuongLienQuan: res.doiTuongLienQuan,
          donViLienQuan: res.donViLienQuan,
          diaDiem: res.diaDiem,
          ngayXayRa: new Date(res.ngayXayRa),
          moTaDienBien: res.moTaDienBien,
          ketQuaXuLy: res.ketQuaXuLy,
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
    if (payload.ngayXayRa instanceof Date) {
      payload.ngayXayRa = payload.ngayXayRa.toISOString().split('T')[0];
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
    this.messageService.add({severity:'error', summary:'Lỗi Server', detail: err.error?.message || 'Có lỗi xảy ra.'});
  }

  goBack() {
    this.location.back();
  }
}
