package com.example.backend.config;

import com.example.backend.entity.DonVi;
import com.example.backend.entity.User;
import com.example.backend.security.UserRole;
import com.example.backend.repository.DonViRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DonViRepository donViRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (donViRepository.count() == 0 || userRepository.count() == 0) {
            seedData();
        } else {
            log.info("Dữ liệu (Đơn vị & User) đã tồn tại, bỏ qua bước seeding.");
        }
    }

    private void seedData() {
        log.info("Bắt đầu seeding dữ liệu mẫu...");

        // 1. Seed Đơn vị
        DonVi boTuLenh = createDonVi("DV001", "Bộ Tư lệnh", "CAP_1", null);
        DonVi cucAnNinh = createDonVi("DV002", "Cục Bảo vệ An ninh", "CAP_2", boTuLenh);
        DonVi phongDT1 = createDonVi("DV003", "Phòng Điều tra AN 1", "CAP_3", cucAnNinh);
        DonVi phongDT2 = createDonVi("DV004", "Phòng Điều tra AN 2", "CAP_3", cucAnNinh);

        // 2. Seed Users
        // THU_TRUONG (Toàn ngành)
        createUser("Thủ trưởng Hệ thống", "thutruong@test.com", UserRole.THU_TRUONG.name(), boTuLenh);
        
        // TRUONG_PHONG (Đơn vị Phòng DT1)
        createUser("Trưởng phòng ĐT1", "truongphong@test.com", UserRole.TRUONG_PHONG.name(), phongDT1);
        
        // CBCT (Đơn vị Phòng DT1)
        createUser("Cán bộ ĐT1", "cbct1@test.com", UserRole.CBCT.name(), phongDT1);
        
        // CBCT (Đơn vị Phòng DT2)
        createUser("Cán bộ ĐT2", "cbct2@test.com", UserRole.CBCT.name(), phongDT2);

        log.info("Seeding hoàn tất!");
        log.info("--- TÀI KHOẢN TEST (Password chung: 123456) ---");
        log.info("1. THU_TRUONG: thutruong@test.com");
        log.info("2. TRUONG_PHONG: truongphong@test.com");
        log.info("3. CBCT Đơn vị 1: cbct1@test.com");
        log.info("4. CBCT Đơn vị 2: cbct2@test.com");
    }

    private DonVi createDonVi(String ma, String ten, String cap, DonVi parent) {
        DonVi dv = new DonVi();
        dv.setMaDonVi(ma);
        dv.setTenDonVi(ten);
        dv.setCapDonVi(cap);
        dv.setParent(parent);
        dv.setActive(true);
        dv.setCreatedAt(LocalDateTime.now());
        return donViRepository.save(dv);
    }

    private void createUser(String name, String email, String role, DonVi donVi) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setDonVi(donVi);
        user.setPasswordHash(passwordEncoder.encode("123456"));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
