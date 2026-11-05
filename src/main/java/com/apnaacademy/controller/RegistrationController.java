package com.apnaacademy.controller;

import com.apnaacademy.dto.RegistrationForm;
import com.apnaacademy.service.AcademyDataService;
import com.apnaacademy.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {
    private final RegistrationService registration;
    private final AcademyDataService data;
    public RegistrationController(RegistrationService registration, AcademyDataService data) {
        this.registration = registration;
        this.data = data;
    }

    @GetMapping("/register")
    public String form(Model model) {
        model.addAttribute("form", new RegistrationForm());
        model.addAttribute("courses", data.list("courses"));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult errors,
                           Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("courses", data.list("courses"));
            return "register";
        }
        try {
            registration.register(form);
            redirect.addFlashAttribute("success", "Registration complete. You can sign in now.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("courses", data.list("courses"));
            model.addAttribute("formError", ex.getMessage());
            return "register";
        }
    }
}
