package com.apnaacademy.controller;

import com.apnaacademy.entity.UserAccount;
import com.apnaacademy.repository.StudentRepository;
import com.apnaacademy.repository.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    private final UserAccountRepository users;
    private final StudentRepository students;
    private final PasswordEncoder passwords;
    private final JdbcTemplate jdbc;

    public ProfileController(UserAccountRepository users, StudentRepository students, PasswordEncoder passwords, JdbcTemplate jdbc) {
        this.users = users;
        this.students = students;
        this.passwords = passwords;
        this.jdbc = jdbc;
    }

    @GetMapping("/student/profile")
    public String studentProfile(Authentication authentication, Model model) {
        model.addAttribute("profile", jdbc.queryForMap("SELECT s.*, c.course_name FROM students s LEFT JOIN courses c ON c.id = s.course_id WHERE s.email = ?", authentication.getName()));
        model.addAttribute("area", "student");
        return "account/profile";
    }

    @GetMapping("/admin/profile")
    public String adminProfile(Authentication authentication, Model model) {
        UserAccount account = users.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        model.addAttribute("profile", java.util.Map.of("name", account.getName(), "email", account.getEmail(), "role", account.getRole()));
        model.addAttribute("area", "admin");
        return "account/profile";
    }

    @PostMapping({"/student/change-password", "/admin/change-password"})
    public String changePassword(Authentication authentication, @RequestParam String currentPassword,
                                 @RequestParam String newPassword, @RequestParam String confirmPassword,
                                 RedirectAttributes redirect) {
        UserAccount account = users.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        if (!passwords.matches(currentPassword, account.getPassword())) {
            redirect.addFlashAttribute("error", "Current password is incorrect.");
        } else if (newPassword.length() < 8 || newPassword.length() > 72 || !newPassword.equals(confirmPassword)) {
            redirect.addFlashAttribute("error", "Use an 8–72 character password and make both new-password fields match.");
        } else {
            account.setPassword(passwords.encode(newPassword));
            users.save(account);
            redirect.addFlashAttribute("success", "Password changed.");
        }
        return "redirect:/" + (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ? "admin" : "student") + "/profile";
    }
}
