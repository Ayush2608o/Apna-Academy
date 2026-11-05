package com.apnaacademy.admin.controller;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {
    private final JdbcTemplate jdbc;
    private static final List<String> COUNTS = List.of("students", "courses", "enquiries", "events");

    public AdminDashboardController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("stats", COUNTS.stream().map(table -> Map.of("label", title(table), "value", jdbc.queryForObject("SELECT COUNT(*) FROM `" + table + "`", Long.class))).toList());
        return "admin/dashboard";
    }

    private String title(String table) { return Character.toUpperCase(table.charAt(0)) + table.substring(1); }
}
