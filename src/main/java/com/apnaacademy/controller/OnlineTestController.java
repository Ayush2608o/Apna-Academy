package com.apnaacademy.controller;

import com.apnaacademy.entity.Student;
import com.apnaacademy.repository.StudentRepository;
import com.apnaacademy.service.AcademyDataService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OnlineTestController {
    private final AcademyDataService data;
    private final StudentRepository students;
    private final JdbcTemplate jdbc;

    public OnlineTestController(AcademyDataService data, StudentRepository students, JdbcTemplate jdbc) {
        this.data = data;
        this.students = students;
        this.jdbc = jdbc;
    }

    @GetMapping("/tests")
    public String tests(Model model) { model.addAttribute("tests", data.tests()); return "tests"; }

    @GetMapping("/tests/{id}")
    public String test(@PathVariable long id, Model model) {
        model.addAttribute("test", data.test(id));
        model.addAttribute("questions", data.questions(id));
        return "test-taking";
    }

    @PostMapping("/tests/{id}/submit")
    public String submit(@PathVariable long id, @RequestParam Map<String, String> form,
                         Authentication authentication, RedirectAttributes redirect) {
        Student student = students.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        Map<Long, String> answers = new HashMap<>();
        form.forEach((key, value) -> {
            if (key.startsWith("q_")) {
                try { answers.put(Long.parseLong(key.substring(2)), value); }
                catch (NumberFormatException ignored) { }
            }
        });
        BigDecimal score = data.submitTest(id, student.getId(), answers);
        redirect.addFlashAttribute("score", score);
        return "redirect:/student/results";
    }

    @GetMapping("/student/results")
    public String results(Authentication authentication, Model model) {
        Student student = students.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Student profile not found"));
        model.addAttribute("results", jdbc.queryForList("SELECT r.*, t.title AS test_title FROM test_results r JOIN tests t ON t.id = r.test_id WHERE r.student_id = ? ORDER BY r.submitted_at DESC", student.getId()));
        return "student/results";
    }
}
