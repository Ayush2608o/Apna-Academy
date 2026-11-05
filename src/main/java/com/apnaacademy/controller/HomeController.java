package com.apnaacademy.controller;

import com.apnaacademy.service.AcademyDataService;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final AcademyDataService data;
    public HomeController(AcademyDataService data) { this.data = data; }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("stats", data.dashboardStats());
        model.addAttribute("courses", data.list("courses"));
        model.addAttribute("faculty", data.list("faculty"));
        model.addAttribute("events", data.list("events"));
        return "home";
    }

    @GetMapping("/about")
    public String about() { return "about"; }

    @GetMapping("/after-login")
    public String afterLogin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? "redirect:/admin" : "redirect:/student";
    }
}
