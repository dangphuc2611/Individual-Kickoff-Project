import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileUploadEvent } from 'primeng/fileupload';
import { FileUploadModule } from 'primeng/fileupload';
import { HttpClient, HttpEventType, HttpRequest, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule, FileUploadModule],
  providers: [MessageService],
  template: `
    <div class="card p-fluid">
      <p-fileUpload 
        name="demo[]" 
        [customUpload]="true" 
        (uploadHandler)="onUpload($event)"
        [multiple]="multiple" 
        accept="{{ accept }}" 
        [maxFileSize]="maxFileSize"
        [fileLimit]="fileLimit"
        (onSelect)="onSelect($event)"
        [chooseLabel]="'Chọn File'"
        [uploadLabel]="'Tải Lên'"
        [cancelLabel]="'Hủy'"
        invalidFileSizeMessageSummary="{0}: Vượt quá dung lượng"
        invalidFileSizeMessageDetail="Kích thước tối đa là {0}MB."
        invalidFileTypeMessageSummary="{0}: Định dạng không hợp lệ"
        invalidFileTypeMessageDetail="File hợp lệ: {0}.">
      </p-fileUpload>
      
      <!-- Custom Progress Bar (Nếu PrimeNG chưa cover hết trường hợp upload API) -->
      <div *ngIf="uploading" class="mt-4">
        <div class="flex justify-between items-center mb-1">
          <span class="text-sm font-medium text-blue-700">Đang tải lên...</span>
          <span class="text-sm font-medium text-blue-700">{{ progress }}%</span>
        </div>
        <div class="w-full bg-slate-200 rounded-full h-2.5">
          <div class="bg-blue-600 h-2.5 rounded-full" [style.width]="progress + '%'"></div>
        </div>
      </div>
    </div>
  `
})
export class FileUploadComponent {
  @Input() url: string = ''; // Endpoint API (VD: /api/ho-so-dieu-tra/1/files)
  @Input() maxFileSize: number = 52428800; // 50MB
  @Input() multiple: boolean = true;
  @Input() fileLimit: number = 10;
  @Input() accept: string = '.pdf,.doc,.docx,.xls,.xlsx,.png,.jpg,.jpeg';
  
  @Output() onUploadComplete = new EventEmitter<any>();
  @Output() onUploadError = new EventEmitter<any>();

  uploading = false;
  progress = 0;
  uploadSub: Subscription | undefined;

  constructor(private http: HttpClient, private messageService: MessageService) {}

  onSelect(event: any) {
    this.progress = 0;
  }

  onUpload(event: any) {
    if (!this.url) {
      console.error('URL endpoint is missing for FileUploadComponent');
      return;
    }

    const files: File[] = event.files;
    if (!files || files.length === 0) return;

    this.uploading = true;
    this.progress = 0;

    const formData = new FormData();
    for (let file of files) {
      formData.append('file', file, file.name);
    }

    const req = new HttpRequest('POST', this.url, formData, {
      reportProgress: true
    });

    this.uploadSub = this.http.request(req).subscribe({
      next: (httpEvent: HttpEvent<any>) => {
        if (httpEvent.type === HttpEventType.UploadProgress) {
          this.progress = Math.round(100 * (httpEvent.loaded / (httpEvent.total || 1)));
        } else if (httpEvent.type === HttpEventType.Response) {
          this.uploading = false;
          this.progress = 100;
          this.onUploadComplete.emit(httpEvent.body);
          this.messageService.add({severity: 'success', summary: 'Thành công', detail: 'Đã tải file thành công!'});
        }
      },
      error: (err) => {
        this.uploading = false;
        this.progress = 0;
        console.error('Upload Error: ', err);
        this.onUploadError.emit(err);
        this.messageService.add({severity: 'error', summary: 'Lỗi tải lên', detail: err?.error?.message || 'Không thể tải file lên server.'});
      }
    });
  }
}
