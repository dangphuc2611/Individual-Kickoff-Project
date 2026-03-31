package com.example.backend;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableAsync
public class BackendApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		System.setProperty("HOST", dotenv.get("HOST"));
		System.setProperty("PORT", dotenv.get("PORT"));
		System.setProperty("DATABASE", dotenv.get("DATABASE"));
		System.setProperty("USER", dotenv.get("USER"));
		System.setProperty("PASSWORD", dotenv.get("PASSWORD"));
		System.setProperty("JPA_DDL_AUTO", dotenv.get("JPA_DDL_AUTO", "update"));
		System.setProperty("JPA_SHOW_SQL", dotenv.get("JPA_SHOW_SQL", "true"));
		System.setProperty("SERVER_PORT", dotenv.get("SERVER_PORT", "8080"));
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner fixDatabaseSchema(DataSource dataSource) {
		return args -> {
			try (java.sql.Connection conn = dataSource.getConnection();
					java.sql.Statement stmt = conn.createStatement()) {
				// Sửa lỗi schema cũ trên DB từ actions -> action
				stmt.execute("ALTER TABLE ho_so_access_log DROP COLUMN IF EXISTS actions;");
				System.out.println("✅ Đã xử lý thành công dọn dẹp cột 'actions' dư thừa.");
			} catch (Exception e) {
				System.err.println("❌ Không thể dọn dẹp schema (xóa mảng actions): " + e.getMessage());
			}
		};
	}

}
