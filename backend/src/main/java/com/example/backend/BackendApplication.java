package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
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

}
