**ĐẶC TẢ NGHIỆP VỤ MODULE**

**QUẢN LÝ HỒ SƠ ĐIỀU TRA & HÌNH SỰ**

_Quản lý hồ sơ điều tra cơ bản, thông tin hình sự - tội phạm và hồ sơ nghiệp vụ an ninh mạng_

| **Mã module**     | MOD-04                                           |
| ----------------- | ------------------------------------------------ |
| **Dev phụ trách** | **Dev #4**                                       |
| **Phiên bản**     | 1.0 - Kick-off Sprint                            |
| **Hệ thống**      | Phân hệ Quản lý Công tác Bảo vệ An ninh Quân đội |

# I. TỔNG QUAN MODULE

**🎯 Mục tiêu nghiệp vụ**

Module quản lý 3 loại hồ sơ nghiệp vụ nhạy cảm nhất của hệ thống BVANQĐ:

(1) Hồ sơ điều tra cơ bản,

(2) Thông tin hình sự - tội phạm,

(3) Hồ sơ nghiệp vụ an ninh mạng. Đây là dữ liệu mật - cần cơ chế phân quyền cực kỳ chặt chẽ, mọi truy cập đều phải được ghi log đầy đủ.

**🔒 Lưu ý bảo mật đặc biệt**

Tất cả API của module này phải yêu cầu xác thực. Mọi hành động truy cập (kể cả GET) phải được ghi vào bảng access_log. Backend phải kiểm tra phân quyền đơn vị cho mỗi request - không tin tưởng filter từ phía client.

## ▌ Người dùng liên quan

| **Tác nhân**        | **Quyền hạn**                             | **Ghi chú**                      |
| ------------------- | ----------------------------------------- | -------------------------------- |
| Cán bộ chuyên trách | CRUD + Import/Export trên cả 3 loại hồ sơ | Chỉ trong đơn vị được phân quyền |
| Trưởng phòng        | Xem + Tìm kiếm + Export + Xem access log  | Phạm vi phân quyền + log đầy đủ  |
| Thủ trưởng          | Xem chi tiết + Xem tổng hợp (read-only)   | Toàn ngành, có log truy cập      |

## ▌ 3 loại hồ sơ trong module

| **#** | **Loại hồ sơ**                  | **Mô tả nghiệp vụ**                                              | **Tác nhân quản lý**   |
| ----- | ------------------------------- | ---------------------------------------------------------------- | ---------------------- |
| **1** | **Hồ sơ điều tra cơ bản**       | Hồ sơ theo dõi, nắm tình hình đối tượng cần điều tra an ninh     | CBCT phòng Điều tra AN |
| **2** | **Thông tin hình sự, tội phạm** | Dữ liệu ghi nhận các vụ việc hình sự, tội phạm có yếu tố quân sự | CBCT phòng Điều tra AN |
| **3** | **Hồ sơ nghiệp vụ AN mạng**     | Hồ sơ điều tra, quản lý nghiệp vụ thuộc lĩnh vực an ninh mạng    | CBCT Trung tâm AN mạng |

## ▌ Quy tắc nghiệp vụ cốt lõi

- Mọi truy cập (xem, tìm kiếm, export) đều phải ghi access log - đây là yêu cầu bắt buộc, không phải nice-to-have.
- Phân quyền nghiêm ngặt: CBCT chỉ thao tác hồ sơ thuộc đơn vị mình. Backend kiểm tra mỗi request.
- Soft delete bắt buộc - không được xóa cứng bất kỳ hồ sơ nào.
- Phân loại hồ sơ (mức độ mật): Thông thường / Mật / Tối mật. Mức độ mật ảnh hưởng đến quyền xem.
- File đính kèm hồ sơ: tối đa 10 file, mỗi file ≤ 50MB (tài liệu điều tra có thể lớn).
- Lịch sử chỉnh sửa bắt buộc - ghi mọi thay đổi vào audit log.

# II. DANH SÁCH CHỨC NĂNG

## ▌ Nhóm A - Hồ sơ điều tra cơ bản

| **UC#** | **Tên chức năng**                          | **Tác nhân**                   | **Ưu tiên**     |
| ------- | ------------------------------------------ | ------------------------------ | --------------- |
| **A01** | Xem danh sách hồ sơ điều tra cơ bản        | CBCT, Trưởng phòng, Thủ trưởng | 🔴 Phải có      |
| **A02** | Tìm kiếm nhanh / nâng cao                  | CBCT, Trưởng phòng             | 🔴 Phải có      |
| **A03** | Xem chi tiết hồ sơ (ghi access log)        | Tất cả                         | 🔴 Phải có      |
| **A04** | Thêm mới hồ sơ điều tra                    | CBCT                           | 🔴 Phải có      |
| **A05** | Cập nhật hồ sơ                             | CBCT                           | 🔴 Phải có      |
| **A06** | Xóa hồ sơ (soft delete)                    | CBCT                           | 🔴 Phải có      |
| **A07** | Upload / Xóa file tài liệu điều tra        | CBCT                           | 🟠 Nên có       |
| **A08** | Import từ file Excel                       | CBCT                           | 🟠 Nên có       |
| **A09** | Xuất danh sách ra Excel                    | CBCT, Trưởng phòng             | 🟠 Nên có       |
| **A10** | Xem lịch sử chỉnh sửa hồ sơ                | Trưởng phòng                   | 🟡 Nice-to-have |
| **A11** | Xem access log (ai xem hồ sơ này, khi nào) | Trưởng phòng                   | 🟠 Nên có       |

## ▌ Nhóm B - Thông tin hình sự, tội phạm

| **UC#** | **Tên chức năng**               | **Tác nhân**                   | **Ưu tiên** |
| ------- | ------------------------------- | ------------------------------ | ----------- |
| **B01** | Xem danh sách thông tin hình sự | CBCT, Trưởng phòng, Thủ trưởng | 🔴 Phải có  |
| **B02** | Tìm kiếm nhanh / nâng cao       | CBCT, Trưởng phòng             | 🔴 Phải có  |
| **B03** | Xem chi tiết (ghi access log)   | Tất cả                         | 🔴 Phải có  |
| **B04** | Thêm mới thông tin hình sự      | CBCT                           | 🔴 Phải có  |
| **B05** | Cập nhật                        | CBCT                           | 🔴 Phải có  |
| **B06** | Xóa (soft delete)               | CBCT                           | 🔴 Phải có  |
| **B07** | Import / Xuất Excel             | CBCT, Trưởng phòng             | 🟠 Nên có   |

## ▌ Nhóm C - Hồ sơ nghiệp vụ an ninh mạng

| **UC#** | **Tên chức năng**             | **Tác nhân**       | **Ưu tiên** |
| ------- | ----------------------------- | ------------------ | ----------- |
| **C01** | Xem danh sách hồ sơ AN mạng   | CBCT, Trưởng phòng | 🔴 Phải có  |
| **C02** | Tìm kiếm nhanh / nâng cao     | CBCT, Trưởng phòng | 🔴 Phải có  |
| **C03** | Xem chi tiết (ghi access log) | Tất cả             | 🔴 Phải có  |
| **C04** | Thêm mới hồ sơ AN mạng        | CBCT               | 🔴 Phải có  |
| **C05** | Cập nhật / Xóa hồ sơ          | CBCT               | 🔴 Phải có  |
| **C06** | Import / Xuất Excel           | CBCT, Trưởng phòng | 🟠 Nên có   |

# III. ĐẶC TẢ CHI TIẾT

## ▌ A04 - Thêm mới Hồ sơ điều tra cơ bản

| **Tên trường**                  | **Kiểu dữ liệu**   | **Bắt buộc** | **Ghi chú / Validate**                             |
| ------------------------------- | ------------------ | ------------ | -------------------------------------------------- |
| **Mã hồ sơ**                    | Text (auto-gen)    | ✅ Có        | Hệ thống tự tạo theo format: DTRA-YYYY-XXXXX       |
| **Tiêu đề / tên hồ sơ**         | Text               | ✅ Có        | Tối đa 255 ký tự                                   |
| **Phân loại hồ sơ**             | Dropdown danh mục  | ✅ Có        | Điều tra cơ bản / Theo dõi / Đặc biệt              |
| **Mức độ mật**                  | Dropdown           | ✅ Có        | Thông thường / Mật / Tối mật - ảnh hưởng quyền xem |
| **Đối tượng theo dõi (họ tên)** | Text               | ✅ Có        | Tên đầy đủ của đối tượng điều tra                  |
| **Đơn vị đối tượng**            | Text               | ✅ Có        | Đơn vị quân đội của đối tượng                      |
| **Ngày mở hồ sơ**               | Date picker        | ✅ Có        | Mặc định hôm nay, không được > hôm nay             |
| **CBCT phụ trách**              | Auto-fill / Search | ✅ Có        | Mặc định là user đang đăng nhập                    |
| **Nội dung / tình hình**        | Rich text          | ✅ Có        | Mô tả chi tiết tình hình điều tra                  |
| **Trạng thái hồ sơ**            | Dropdown           | ✅ Có        | Đang theo dõi / Tạm dừng / Đã kết thúc             |
| **File tài liệu**               | Multi-upload       | ❌ Không     | ≤ 10 file, ≤ 50MB/file                             |
| **Ghi chú nội bộ**              | Textarea           | ❌ Không     | Tối đa 1.000 ký tự                                 |

## ▌ B04 - Thêm mới Thông tin hình sự tội phạm

| **Tên trường**              | **Kiểu dữ liệu**  | **Bắt buộc** | **Ghi chú / Validate**                                   |
| --------------------------- | ----------------- | ------------ | -------------------------------------------------------- |
| **Mã thông tin**            | Text (auto-gen)   | ✅ Có        | Format: HSTU-YYYY-XXXXX                                  |
| **Tiêu đề vụ việc**         | Text              | ✅ Có        | Tối đa 255 ký tự                                         |
| **Loại tội danh**           | Dropdown danh mục | ✅ Có        | Hình sự / Ma túy / Tham nhũng / Khác                     |
| **Mức độ mật**              | Dropdown          | ✅ Có        | Thông thường / Mật / Tối mật                             |
| **Đối tượng liên quan**     | Text              | ✅ Có        | Tên đầy đủ, có thể nhập nhiều người (ngăn cách dấu phẩy) |
| **Đơn vị liên quan**        | Text              | ✅ Có        |                                                          |
| **Ngày xảy ra / phát hiện** | Date picker       | ✅ Có        | Không được > hôm nay                                     |
| **Địa điểm**                | Text              | ❌ Không     |                                                          |
| **Mô tả diễn biến**         | Rich text         | ✅ Có        |                                                          |
| **Kết quả xử lý**           | Dropdown          | ❌ Không     | Đang xử lý / Đã xử lý / Chuyển cơ quan khác              |
| **File tài liệu**           | Multi-upload      | ❌ Không     | ≤ 10 file, ≤ 50MB/file                                   |

## ▌ Access Log - Yêu cầu bắt buộc

**🔒 Nghiệp vụ bắt buộc**

Mọi hành động GET chi tiết hồ sơ phải ghi vào bảng ho_so_access_log. Đây là yêu cầu kiểm toán bắt buộc - không được bỏ qua dù dưới bất kỳ lý do nào.

### Thông tin cần ghi vào access log

- user_id: ID người truy cập.
- ho_so_id + ho_so_type: ID hồ sơ và loại (DIEU_TRA / HINH_SU / AN_MANG).
- action: VIEW / EXPORT / PRINT.
- ip_address: IP của client.
- accessed_at: Timestamp chính xác đến giây.
- user_agent: Browser/device thực hiện truy cập.

# IV. DATA MODEL GỢI Ý

**Bảng 1: ho_so_dieu_tra**

| **Tên cột**          | **Kiểu**     | **Nullable** | **Mô tả**                       |
| -------------------- | ------------ | ------------ | ------------------------------- |
| **id**               | UUID         | NOT NULL     | PK                              |
| **ma_ho_so**         | VARCHAR(20)  | NOT NULL     | UNIQUE - auto-generated         |
| **tieu_de**          | VARCHAR(255) | NOT NULL     |                                 |
| **phan_loai_id**     | INT FK       | NOT NULL     | → danh mục phân loại            |
| **muc_do_mat**       | ENUM         | NOT NULL     | THUONG / MAT / TOI_MAT          |
| **doi_tuong_ho_ten** | VARCHAR(255) | NOT NULL     |                                 |
| **don_vi_doi_tuong** | VARCHAR(255) | NOT NULL     |                                 |
| **ngay_mo_ho_so**    | DATE         | NOT NULL     |                                 |
| **cbct_phu_trach**   | INT FK       | NOT NULL     | → users                         |
| **noi_dung**         | TEXT         | NOT NULL     | HTML rich text                  |
| **trang_thai**       | ENUM         | NOT NULL     | DANG_THEO_DOI/TAM_DUNG/KET_THUC |
| **don_vi_id**        | INT FK       | NOT NULL     | Đơn vị quản lý hồ sơ            |
| **is_deleted**       | BOOLEAN      | NOT NULL     |                                 |
| **created_by**       | INT FK       | NOT NULL     |                                 |
| **created_at**       | TIMESTAMP    | NOT NULL     |                                 |
| **updated_by**       | INT FK       | NULL         |                                 |
| **updated_at**       | TIMESTAMP    | NULL         |                                 |

**Bảng 2: thong_tin_hinh_su (cấu trúc tương tự - các trường đặc thù)**

- Thêm: loai_toi_danh_id, doi_tuong_lien_quan (TEXT), ket_qua_xu_ly ENUM, ngay_xay_ra DATE, dia_diem VARCHAR.

**Bảng 3: ho_so_an_ninh_mang (cấu trúc tương tự - các trường đặc thù)**

- Thêm: loai_tan_cong (Phishing/Malware/Intrusion/Khác), he_thong_bi_anh_huong, muc_do_thiet_hai, ngay_phat_hien DATE.

**Bảng 4: ho_so_file - dùng chung cho cả 3 loại**

- id | ho_so_id | ho_so_type (DIEU_TRA/HINH_SU/AN_MANG) | file_name | file_path | file_size | mime_type | uploaded_by | uploaded_at | is_deleted

**Bảng 5: ho_so_access_log - bắt buộc**

- id | user_id | ho_so_id | ho_so_type | action (VIEW/EXPORT/PRINT) | ip_address | user_agent | accessed_at

**Bảng 6: ho_so_audit_log - lịch sử chỉnh sửa**

- id | record_id | ho_so_type | changed_by | changed_at | action | field_name | old_value | new_value

# V. API ENDPOINTS GỢI Ý

**Nhóm A - Hồ sơ điều tra cơ bản:**

| **Method** | **Endpoint**                       | **Mô tả**                                                                      |
| ---------- | ---------------------------------- | ------------------------------------------------------------------------------ |
| **GET**    | /api/ho-so/dieu-tra                | Danh sách (filter: muc_do_mat, trang_thai, don_vi_ids\[\], search, date range) |
| **GET**    | /api/ho-so/dieu-tra/:id            | Chi tiết - tự động ghi access log VIEW                                         |
| **POST**   | /api/ho-so/dieu-tra                | Tạo mới - auto-gen ma_ho_so                                                    |
| **PUT**    | /api/ho-so/dieu-tra/:id            | Cập nhật - ghi audit log diff                                                  |
| **DELETE** | /api/ho-so/dieu-tra/:id            | Soft delete                                                                    |
| **GET**    | /api/ho-so/dieu-tra/:id/audit      | Lịch sử chỉnh sửa                                                              |
| **GET**    | /api/ho-so/dieu-tra/:id/access-log | Log truy cập hồ sơ này                                                         |
| **GET**    | /api/ho-so/dieu-tra/export         | Xuất Excel - ghi access log EXPORT                                             |

**_Nhóm B & C: tương tự với prefix /api/ho-so/hinh-su và /api/ho-so/an-ninh-mang_**

**API tổng hợp:**

| **Method** | **Endpoint**                        | **Mô tả**                                                                    |
| ---------- | ----------------------------------- | ---------------------------------------------------------------------------- |
| **GET**    | /api/ho-so/access-log               | Log truy cập tổng hợp (Trưởng phòng xem - filter theo user, loại, thời gian) |
| **POST**   | /api/ho-so/dieu-tra/import/validate | Validate file import hồ sơ điều tra                                          |
| **POST**   | /api/ho-so/dieu-tra/import/confirm  | Xác nhận import                                                              |

# VI. TIÊU CHÍ HOÀN THÀNH

**✅ Sprint hoàn thành khi:**

1\. CRUD đầy đủ cho cả 3 loại hồ sơ (Điều tra / Hình sự / AN mạng).

2\. Access log ghi tự động mỗi khi có GET chi tiết hoặc Export - kiểm tra DB sau mỗi truy cập.

3\. Phân quyền đúng: CBCT chỉ thao tác hồ sơ đơn vị mình; BE kiểm tra mọi request.

4\. Soft delete bắt buộc - không có hard delete ở bất kỳ endpoint nào.

5\. Upload file đến 50MB hoạt động, validate type ngay khi chọn file.

6\. Auto-gen mã hồ sơ đúng format và unique.

7\. Audit log ghi diff (trường nào thay đổi, giá trị cũ → mới).

8\. Import 2 bước (validate + confirm) cho hồ sơ điều tra.