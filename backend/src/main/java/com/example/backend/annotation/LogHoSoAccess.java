package com.example.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogHoSoAccess {
    String type(); // "DIEU_TRA", "HINH_SU", "AN_NINH_MANG"
    String action() default "VIEW";
}
