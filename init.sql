CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    due_date DATE,
    due_time TIME,
    user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Insert dummy data
INSERT INTO users (name, email, role) VALUES 
('Alice Smith', 'alice@example.com', 'Admin'),
('Bob Johnson', 'bob@example.com', 'User');

INSERT INTO tasks (title, description, status, priority, due_date, due_time, user_id) VALUES 
('Setup Database', 'Configure PostgreSQL in Docker', 'DONE', 'HIGH', '2026-03-18', '10:00:00', 1),
('Implement User API', 'Create CRUD endpoints for Users', 'IN_PROGRESS', 'HIGH', '2026-03-19', '15:00:00', 1),
('Design Frontend UI', 'Create mockups using TailwindCSS', 'TODO', 'MEDIUM', '2026-03-20', '17:00:00', 2);


-- ============================================================
-- MOD-04: Quản lý Hồ sơ Điều tra & Hình sự
-- Database: PostgreSQL
-- ============================================================

-- ============================================================
-- SỬA BẢNG HIỆN CÓ
-- ============================================================

-- Bảng users: thêm các cột cần thiết cho phân quyền
ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS don_vi_id     BIGINT,
    ADD COLUMN IF NOT EXISTS is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS updated_at    TIMESTAMP DEFAULT NOW();

-- Bảng tasks: không liên quan MOD-04, giữ nguyên
-- (tasks là module riêng, không cần sửa cho MOD-04)

-- ============================================================
-- BẢNG DANH MỤC ĐƠNG VỊ
-- ============================================================
CREATE TABLE IF NOT EXISTS public.don_vi (
    id          BIGSERIAL PRIMARY KEY,
    ma_don_vi   VARCHAR(50)  NOT NULL UNIQUE,
    ten_don_vi  VARCHAR(255) NOT NULL,
    cap_don_vi  VARCHAR(50),              -- Trung ương / Quân khu / Đơn vị cơ sở
    parent_id   BIGINT REFERENCES public.don_vi(id),
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- BẢNG 1: HỒ SƠ ĐIỀU TRA CƠ BẢN
-- ============================================================
CREATE TABLE IF NOT EXISTS public.ho_so_dieu_tra (
    id                  BIGSERIAL    PRIMARY KEY,
    ma_ho_so            VARCHAR(20)  NOT NULL UNIQUE,   -- DTRA-YYYY-XXXXX
    tieu_de             VARCHAR(255) NOT NULL,
    phan_loai           VARCHAR(50)  NOT NULL           -- Điều tra cơ bản / Theo dõi / Đặc biệt
                            CHECK (phan_loai IN ('DIEU_TRA_CO_BAN','THEO_DOI','DAC_BIET')),
    muc_do_mat          VARCHAR(20)  NOT NULL DEFAULT 'THUONG'
                            CHECK (muc_do_mat IN ('THUONG','MAT','TOI_MAT')),
    doi_tuong_ho_ten    VARCHAR(255) NOT NULL,
    don_vi_doi_tuong    VARCHAR(255) NOT NULL,
    ngay_mo_ho_so       DATE         NOT NULL,
    cbct_phu_trach      BIGINT       NOT NULL REFERENCES public.users(id),
    noi_dung            TEXT         NOT NULL,          -- HTML rich text
    trang_thai          VARCHAR(30)  NOT NULL DEFAULT 'DANG_THEO_DOI'
                            CHECK (trang_thai IN ('DANG_THEO_DOI','TAM_DUNG','KET_THUC')),
    ghi_chu             VARCHAR(1000),
    don_vi_id           BIGINT       NOT NULL REFERENCES public.don_vi(id),
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by          BIGINT       NOT NULL REFERENCES public.users(id),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by          BIGINT       REFERENCES public.users(id),
    updated_at          TIMESTAMP
);

-- ============================================================
-- BẢNG 2: THÔNG TIN HÌNH SỰ – TỘI PHẠM
-- ============================================================
CREATE TABLE IF NOT EXISTS public.thong_tin_hinh_su (
    id                  BIGSERIAL    PRIMARY KEY,
    ma_thong_tin        VARCHAR(20)  NOT NULL UNIQUE,   -- HSTU-YYYY-XXXXX
    tieu_de             VARCHAR(255) NOT NULL,
    loai_toi_danh       VARCHAR(50)  NOT NULL
                            CHECK (loai_toi_danh IN ('HINH_SU','MA_TUY','THAM_NHUNG','KHAC')),
    muc_do_mat          VARCHAR(20)  NOT NULL DEFAULT 'THUONG'
                            CHECK (muc_do_mat IN ('THUONG','MAT','TOI_MAT')),
    doi_tuong_lien_quan TEXT         NOT NULL,          -- Nhiều người, cách nhau dấu phẩy
    don_vi_lien_quan    VARCHAR(255) NOT NULL,
    ngay_xay_ra         DATE         NOT NULL,
    dia_diem            VARCHAR(255),
    mo_ta_dien_bien     TEXT         NOT NULL,
    ket_qua_xu_ly       VARCHAR(30)
                            CHECK (ket_qua_xu_ly IN ('DANG_XU_LY','DA_XU_LY','CHUYEN_CO_QUAN_KHAC')),
    don_vi_id           BIGINT       NOT NULL REFERENCES public.don_vi(id),
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by          BIGINT       NOT NULL REFERENCES public.users(id),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by          BIGINT       REFERENCES public.users(id),
    updated_at          TIMESTAMP
);

-- ============================================================
-- BẢNG 3: HỒ SƠ NGHIỆP VỤ AN NINH MẠNG
-- ============================================================
CREATE TABLE IF NOT EXISTS public.ho_so_an_ninh_mang (
    id                      BIGSERIAL    PRIMARY KEY,
    ma_ho_so                VARCHAR(20)  NOT NULL UNIQUE,  -- ANMG-YYYY-XXXXX
    tieu_de                 VARCHAR(255) NOT NULL,
    loai_tan_cong           VARCHAR(50)  NOT NULL
                                CHECK (loai_tan_cong IN ('PHISHING','MALWARE','INTRUSION','KHAC')),
    muc_do_mat              VARCHAR(20)  NOT NULL DEFAULT 'THUONG'
                                CHECK (muc_do_mat IN ('THUONG','MAT','TOI_MAT')),
    he_thong_bi_anh_huong   TEXT         NOT NULL,
    muc_do_thiet_hai        VARCHAR(50),
    ngay_phat_hien          DATE         NOT NULL,
    mo_ta_chi_tiet          TEXT         NOT NULL,
    trang_thai              VARCHAR(30)  NOT NULL DEFAULT 'DANG_XU_LY'
                                CHECK (trang_thai IN ('DANG_XU_LY','DA_XU_LY','TAM_DUNG')),
    don_vi_id               BIGINT       NOT NULL REFERENCES public.don_vi(id),
    is_deleted              BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by              BIGINT       NOT NULL REFERENCES public.users(id),
    created_at              TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by              BIGINT       REFERENCES public.users(id),
    updated_at              TIMESTAMP
);

-- ============================================================
-- BẢNG 4: FILE ĐÍNH KÈM (dùng chung 3 loại hồ sơ)
-- ============================================================
CREATE TABLE IF NOT EXISTS public.ho_so_file (
    id          BIGSERIAL    PRIMARY KEY,
    ho_so_id    BIGINT       NOT NULL,
    ho_so_type  VARCHAR(20)  NOT NULL
                    CHECK (ho_so_type IN ('DIEU_TRA','HINH_SU','AN_MANG')),
    file_name   VARCHAR(255) NOT NULL,
    file_path   TEXT         NOT NULL,
    file_size   BIGINT       NOT NULL,   -- bytes, max 50MB = 52_428_800
    mime_type   VARCHAR(100) NOT NULL,
    uploaded_by BIGINT       NOT NULL REFERENCES public.users(id),
    uploaded_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ============================================================
-- BẢNG 5: ACCESS LOG (bắt buộc - mọi GET đều ghi)
-- ============================================================
CREATE TABLE IF NOT EXISTS public.ho_so_access_log (
    id          BIGSERIAL   PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES public.users(id),
    ho_so_id    BIGINT      NOT NULL,
    ho_so_type  VARCHAR(20) NOT NULL
                    CHECK (ho_so_type IN ('DIEU_TRA','HINH_SU','AN_MANG')),
    action      VARCHAR(20) NOT NULL
                    CHECK (action IN ('VIEW','EXPORT','PRINT')),
    ip_address  VARCHAR(50),
    user_agent  TEXT,
    accessed_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- BẢNG 6: AUDIT LOG (lịch sử chỉnh sửa)
-- ============================================================
CREATE TABLE IF NOT EXISTS public.ho_so_audit_log (
    id          BIGSERIAL   PRIMARY KEY,
    record_id   BIGINT      NOT NULL,
    ho_so_type  VARCHAR(20) NOT NULL
                    CHECK (ho_so_type IN ('DIEU_TRA','HINH_SU','AN_MANG')),
    changed_by  BIGINT      NOT NULL REFERENCES public.users(id),
    changed_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    action      VARCHAR(20) NOT NULL
                    CHECK (action IN ('CREATE','UPDATE','DELETE')),
    field_name  VARCHAR(100),
    old_value   TEXT,
    new_value   TEXT
);

-- ============================================================
-- INDEX (tối ưu query thường dùng)
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_dieu_tra_don_vi    ON public.ho_so_dieu_tra(don_vi_id) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_dieu_tra_trang_thai ON public.ho_so_dieu_tra(trang_thai) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_dieu_tra_mat       ON public.ho_so_dieu_tra(muc_do_mat) WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_hinh_su_don_vi     ON public.thong_tin_hinh_su(don_vi_id) WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_an_mang_don_vi     ON public.ho_so_an_ninh_mang(don_vi_id) WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_access_log_ho_so   ON public.ho_so_access_log(ho_so_id, ho_so_type);
CREATE INDEX IF NOT EXISTS idx_access_log_user    ON public.ho_so_access_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_record   ON public.ho_so_audit_log(record_id, ho_so_type);

CREATE INDEX IF NOT EXISTS idx_file_ho_so         ON public.ho_so_file(ho_so_id, ho_so_type) WHERE is_deleted = FALSE;