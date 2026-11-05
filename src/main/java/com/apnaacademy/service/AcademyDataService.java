package com.apnaacademy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcademyDataService {
    private static final Set<String> PUBLIC_TABLES = Set.of("courses", "faculty", "events", "jobs", "placements", "notes", "tests");
    private final JdbcTemplate jdbc;

    public AcademyDataService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Map<String, Object>> list(String table) {
        requirePublicTable(table);
        String where = table.equals("jobs") ? " WHERE status = 'ACTIVE'" : "";
        return jdbc.queryForList("SELECT * FROM `" + table + "`" + where + " ORDER BY id DESC LIMIT 100");
    }

    public Map<String, Object> find(String table, long id) {
        requirePublicTable(table);
        return jdbc.queryForMap("SELECT * FROM `" + table + "` WHERE id = ?", id);
    }

    public List<Map<String, Object>> dashboardStats() {
        return List.of(
                Map.of("label", "Courses", "value", count("courses")),
                Map.of("label", "Faculty", "value", count("faculty")),
                Map.of("label", "Students", "value", count("students")),
                Map.of("label", "Events", "value", count("events")));
    }

    private long count(String table) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM `" + table + "`", Long.class);
    }

    private void requirePublicTable(String table) {
        if (!PUBLIC_TABLES.contains(table)) throw new IllegalArgumentException("Unsupported content table");
    }

    public List<Map<String, Object>> verifyCertificate(String certificateNumber) {
        return jdbc.queryForList("SELECT cert.certificate_number, s.name AS student_name, " +
                "c.course_name, cert.issue_date, cert.status " +
                "FROM certificates cert " +
                "JOIN students s ON s.id = cert.student_id " +
                "LEFT JOIN courses c ON c.id = cert.course_id " +
                "WHERE cert.certificate_number = ?", certificateNumber);
    }

    public boolean hasCertificates() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM certificates", Long.class) > 0;
    }

    public Map<String, Object> certificate(String certificateNumber) {
        return jdbc.queryForMap("SELECT cert.certificate_number, s.name AS student_name, c.course_name, " +
                "cert.issue_date, cert.status " +
                "FROM certificates cert " +
                "JOIN students s ON s.id = cert.student_id " +
                "LEFT JOIN courses c ON c.id = cert.course_id " +
                "WHERE cert.certificate_number = ?", certificateNumber);
    }

    public List<Map<String, Object>> certificateStudents() {
        return jdbc.queryForList("SELECT s.id, s.name, s.email, c.course_name " +
                "FROM students s JOIN courses c ON c.id = s.course_id ORDER BY s.name");
    }

    @Transactional
    public String issueCertificate(long studentId) {
        Long courseId = jdbc.queryForObject("SELECT course_id FROM students WHERE id = ?", Long.class, studentId);
        if (courseId == null) {
            throw new IllegalArgumentException("Assign a course to this student before issuing a certificate.");
        }

        String certificateNumber = "APNA-" + java.time.Year.now().getValue() + "-" +
                java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase(java.util.Locale.ROOT);
        jdbc.update("INSERT INTO certificates (student_id, course_id, certificate_number, issue_date, status) " +
                        "VALUES (?, ?, ?, CURRENT_DATE(), 'ISSUED')",
                studentId, courseId, certificateNumber);
        return certificateNumber;
    }

    @Transactional
    public void saveEnquiry(String name, String email, String phone, String course, String message) {
        jdbc.update("INSERT INTO enquiries (name, email, phone, course_interest, message, status) VALUES (?, ?, ?, ?, ?, 'PENDING')",
                name, blankToNull(email), blankToNull(phone), blankToNull(course), blankToNull(message));
    }

    public List<Map<String, Object>> tests() {
        return jdbc.queryForList("SELECT id, course_id, title, description, duration_minutes, total_marks FROM tests WHERE status = 'ACTIVE' ORDER BY id DESC");
    }

    public Map<String, Object> test(long id) {
        return jdbc.queryForMap("SELECT id, course_id, title, description, duration_minutes, total_marks FROM tests WHERE id = ? AND status = 'ACTIVE'", id);
    }

    public List<Map<String, Object>> questions(long testId) {
        return jdbc.queryForList("SELECT id, question, option_a, option_b, option_c, option_d, marks FROM test_questions WHERE test_id = ? ORDER BY id", testId);
    }

    @Transactional
    public BigDecimal submitTest(long testId, long studentId, Map<Long, String> answers) {
        List<Map<String, Object>> questions = jdbc.queryForList("SELECT id, correct_option, marks FROM test_questions WHERE test_id = ?", testId);
        BigDecimal score = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> question : questions) {
            BigDecimal marks = (BigDecimal) question.get("marks");
            if (marks == null) marks = BigDecimal.ONE;
            total = total.add(marks);
            String chosen = answers.get(((Number) question.get("id")).longValue());
            if (chosen != null && chosen.equalsIgnoreCase((String) question.get("correct_option"))) score = score.add(marks);
        }
        jdbc.update("INSERT INTO test_results (test_id, student_id, obtained_marks, total_marks, started_at, submitted_at, status) " +
                        "VALUES (?, ?, ?, ?, NOW(), NOW(), 'SUBMITTED')", testId, studentId, score, total);
        return score;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
