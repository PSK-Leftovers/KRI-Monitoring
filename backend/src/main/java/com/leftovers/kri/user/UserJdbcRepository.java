package com.leftovers.kri.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean existsByEmail(String email) {
        // Parameterized query explicitly used to satisfy SQL Injection protection requirements
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}