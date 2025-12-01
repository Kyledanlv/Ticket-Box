package com.ticketbox.analytics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/ping")
    public String ping() {
        return "Analytics Service is running! Time: " + new Date();
    }

    @GetMapping("/database")
    public String testDatabase() {
        try {
            String dbName = jdbcTemplate.queryForObject(
                    "SELECT DATABASE()", String.class);
            return "Connected to database: " + dbName;
        } catch (Exception e) {
            return "Database error: " + e.getMessage();
        }
    }

    @GetMapping("/tables")
    public List<String> listTables() {
        return jdbcTemplate.queryForList(
                "SHOW TABLES", String.class);
    }

    @GetMapping("/events/count")
    public Map<String, Object> countEvents() {
        return jdbcTemplate.queryForMap(
                "SELECT COUNT(*) as count FROM events");
    }
}