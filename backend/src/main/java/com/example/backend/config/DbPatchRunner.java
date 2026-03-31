package com.example.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbPatchRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("Executing DB patches to resolve legacy constraints...");
        
        try {
            jdbcTemplate.execute("ALTER TABLE ho_so_access_log DROP COLUMN IF EXISTS actions;");
            log.info("Patch 1: Dropped legacy column 'actions' from ho_so_access_log.");
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("Patch 1 Failed: {}", e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE ho_so_access_log ALTER COLUMN ho_so_id DROP NOT NULL;");
            log.info("Patch 2: Modified 'ho_so_id' to DROP NOT NULL in ho_so_access_log.");
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("Patch 2 Failed: {}", e.getMessage());
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE ho_so_access_log ALTER COLUMN action DROP NOT NULL;");
            log.info("Patch 3: Modified 'action' to DROP NOT NULL in ho_so_access_log.");
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("Patch 3 Failed: {}", e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE ho_so_audit_log DROP COLUMN IF EXISTS actions;");
            log.info("Patch 4: Dropped legacy column 'actions' from ho_so_audit_log.");
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("Patch 4 Failed: {}", e.getMessage());
        }
    }
}
