package com.example.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("API Hệ thống Quản lý Hồ sơ Điều tra & Hình sự (MOD-04)")
                        .version("1.0")
                        .description("Tài liệu hướng dẫn sử dụng API cho Phân hệ Quản lý Công tác Bảo vệ An ninh Quân đội. " +
                                "Hệ thống hỗ trợ quản lý hồ sơ Điều tra cơ bản, Thông tin hình sự và An ninh mạng.")
                        .contact(new Contact()
                                .name("Đội ngũ Phát triển")
                                .email("support@bvan.gov.vn")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
