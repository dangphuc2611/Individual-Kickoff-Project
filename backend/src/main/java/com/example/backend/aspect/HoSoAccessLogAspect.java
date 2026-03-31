package com.example.backend.aspect;

import com.example.backend.annotation.LogHoSoAccess;
import com.example.backend.security.SecurityUtils;
import com.example.backend.service.HoSoAccessLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class HoSoAccessLogAspect {

    private final HoSoAccessLogService accessLogService;

    @Before("@annotation(com.example.backend.annotation.LogHoSoAccess)")
    public void logAccess(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogHoSoAccess annotation = method.getAnnotation(LogHoSoAccess.class);

            String hoSoType = annotation.type();
            String action = annotation.action();

            Long hoSoId = null;
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    if (arg instanceof Long) {
                        hoSoId = (Long) arg;
                        break;
                    }
                }
            }

            Long currentUserId = null;
            try {
                currentUserId = SecurityUtils.getCurrentUserId();
            } catch (Exception ignored) {}

            HttpServletRequest request = null;
            try {
                ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (sra != null) {
                    request = sra.getRequest();
                }
            } catch (Exception ignored) {}

            if (hoSoId != null && request != null) {
                accessLogService.logAccess(currentUserId, hoSoId, hoSoType, action, request);
            } else if (hoSoId == null) {
                log.warn("HoSoAccessLogAspect: Không tìm thấy tham số ID kiểu Long trong hàm {}", method.getName());
            }
        } catch (Exception e) {
            log.error("Lỗi khi chạy HoSoAccessLogAspect: {}", e.getMessage());
        }
    }
}
