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

		// Fallback to backend directory if variables are missing
		if (dotenv.get("DB_HOST") == null) {
			dotenv = Dotenv.configure()
					.directory("./backend")
					.ignoreIfMissing()
					.load();
		}

		// Set all variables from .env as system properties
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		// Fallback defaults for critical properties if still not set
		if (System.getProperty("JPA_DDL_AUTO") == null)
			System.setProperty("JPA_DDL_AUTO", "update");
		if (System.getProperty("JPA_SHOW_SQL") == null)
			System.setProperty("JPA_SHOW_SQL", "true");
		if (System.getProperty("PORT") == null)
			System.setProperty("PORT", "8080");

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
