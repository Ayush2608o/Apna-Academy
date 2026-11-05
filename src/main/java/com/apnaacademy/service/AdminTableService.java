package com.apnaacademy.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminTableService {
    private static final Map<String, String> MODULES = Map.ofEntries(
            Map.entry("students", "students"), Map.entry("admissions", "students"),
            Map.entry("courses", "courses"), Map.entry("faculty", "faculty"),
            Map.entry("fees", "fees"), Map.entry("attendance", "attendance"),
            Map.entry("enquiries", "enquiries"), Map.entry("placements", "placements"),
            Map.entry("jobs", "jobs"), Map.entry("events", "events"),
            Map.entry("certificates", "certificates"), Map.entry("notes", "notes"),
            Map.entry("tests", "tests"), Map.entry("questions", "test_questions"),
            Map.entry("test-results", "test_results"));

    private final JdbcTemplate jdbc;
    public AdminTableService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public String resolve(String module) {
        String table = MODULES.get(module);
        if (table == null) throw new IllegalArgumentException("Unsupported admin module");
        return table;
    }

    public String title(String module) {
        return switch (module) {
            case "test-results" -> "Test Results";
            case "questions" -> "Test Questions";
            default -> Character.toUpperCase(module.charAt(0)) + module.substring(1);
        };
    }

    public List<Map<String, Object>> columns(String table) {
        resolveTable(table);
        return jdbc.queryForList("SELECT COLUMN_NAME AS name, DATA_TYPE AS dataType, IS_NULLABLE AS nullable, " +
                "COLUMN_DEFAULT AS defaultValue, EXTRA AS extra FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION", table);
    }

    public List<Map<String, Object>> rows(String table) {
        resolveTable(table);
        return jdbc.queryForList("SELECT * FROM `" + table + "` ORDER BY id DESC LIMIT 250");
    }

    public void insert(String table, Map<String, String> submitted) {
        List<String> editable = editableColumns(table);
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        for (String column : editable) {
            String value = submitted.get(column);
            if (value != null && !value.isBlank()) values.put(column, value.trim());
        }
        if (values.isEmpty()) throw new IllegalArgumentException("No fields provided");
        String names = values.keySet().stream().map(name -> "`" + name + "`").collect(Collectors.joining(", "));
        String placeholders = values.keySet().stream().map(name -> "?").collect(Collectors.joining(", "));
        jdbc.update("INSERT INTO `" + table + "` (" + names + ") VALUES (" + placeholders + ")", values.values().toArray());
    }

    public void update(String table, long id, Map<String, String> submitted) {
        List<String> editable = editableColumns(table);
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        for (String column : editable) {
            String value = submitted.get(column);
            if (value != null && !value.isBlank()) values.put(column, value.trim());
        }
        if (values.isEmpty()) throw new IllegalArgumentException("No fields provided");
        String assignments = values.keySet().stream().map(name -> "`" + name + "` = ?").collect(Collectors.joining(", "));
        List<Object> params = new ArrayList<>(values.values());
        params.add(id);
        jdbc.update("UPDATE `" + table + "` SET " + assignments + " WHERE id = ?", params.toArray());
    }

    public void delete(String table, long id) {
        resolveTable(table);
        jdbc.update("DELETE FROM `" + table + "` WHERE id = ?", id);
    }

    private List<String> editableColumns(String table) {
        return columns(table).stream().filter(column -> !"id".equals(column.get("name")))
                .filter(column -> !String.valueOf(column.get("extra")).contains("auto_increment"))
                .map(column -> (String) column.get("name")).toList();
    }

    private void resolveTable(String table) {
        if (!MODULES.containsValue(table)) throw new IllegalArgumentException("Unsupported admin table");
    }
}
