package com.example.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class cung cấp thông tin user hiện tại từ SecurityContext.
 * Dùng khắp Service layer để lấy currentUser mà không cần inject request/session.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Lấy CustomUserDetails của user đang đăng nhập.
     * Throws IllegalStateException nếu chưa authenticate.
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalStateException("Không có user đang đăng nhập trong SecurityContext");
        }
        return (CustomUserDetails) auth.getPrincipal();
    }

    /**
     * Lấy ID của user đang đăng nhập.
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Lấy role của user đang đăng nhập.
     */
    public static String getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    /**
     * Lấy don_vi_id của user đang đăng nhập.
     */
    public static Long getCurrentUserDonViId() {
        return getCurrentUser().getDonViId();
    }

    /**
     * Kiểm tra user hiện tại có role THU_TRUONG không.
     */
    public static boolean isThuTruong() {
        return UserRole.THU_TRUONG.name().equals(getCurrentUserRole());
    }

    /**
     * Kiểm tra user hiện tại có role TRUONG_PHONG trở lên không.
     */
    public static boolean isTruongPhongOrAbove() {
        String role = getCurrentUserRole();
        return UserRole.TRUONG_PHONG.name().equals(role) || UserRole.THU_TRUONG.name().equals(role);
    }

    /**
     * Kiểm tra user hiện tại có role CBCT không.
     */
    public static boolean isCbct() {
        return UserRole.CBCT.name().equals(getCurrentUserRole());
    }
}
