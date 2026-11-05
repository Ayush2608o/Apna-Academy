package com.apnaacademy.controller;

import com.apnaacademy.entity.Student;
import com.apnaacademy.repository.StudentRepository;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentDashboardController {
    private final StudentRepository students;
    private final JdbcTemplate jdbc;

    public StudentDashboardController(StudentRepository students, JdbcTemplate jdbc) {
        this.students = students;
        this.jdbc = jdbc;
    }

    @GetMapping("/student")
    public String dashboard(Authentication authentication, Model model) {
        Student student = students.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        model.addAttribute("student", student);
        model.addAttribute("fees", jdbc.queryForList("SELECT * FROM fees WHERE student_id = ? ORDER BY id DESC", student.getId()));
        model.addAttribute("attendance", jdbc.queryForList("SELECT * FROM attendance WHERE student_id = ? ORDER BY attendance_date DESC LIMIT 30", student.getId()));
        model.addAttribute("certificates", jdbc.queryForList("SELECT * FROM certificates WHERE student_id = ? ORDER BY issue_date DESC", student.getId()));
        model.addAttribute("notes", student.getCourseId() == null ? java.util.List.of() : jdbc.queryForList("SELECT * FROM notes WHERE course_id = ? ORDER BY uploaded_at DESC", student.getCourseId()));
        if (student.getCourseId() != null) {
            Map<String, Object> course = jdbc.queryForMap("SELECT * FROM courses WHERE id = ?", student.getCourseId());
            model.addAttribute("course", course);
        }
        return "student/dashboard";
    }
}
