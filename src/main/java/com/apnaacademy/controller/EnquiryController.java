package com.apnaacademy.controller;

import com.apnaacademy.service.AcademyDataService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Validated
public class EnquiryController {
    private final AcademyDataService data;
    public EnquiryController(AcademyDataService data) { this.data = data; }

    @GetMapping("/contact")
    public String contact() { return "contact"; }
    @GetMapping("/enquiry")
    public String enquiry(Model model) { model.addAttribute("courses", data.list("courses")); return "enquiry"; }

    @PostMapping({"/contact", "/enquiry"})
    public String submit(@RequestParam @NotBlank @Size(max = 150) String name,
                         @RequestParam(required = false) @Email @Size(max = 150) String email,
                         @RequestParam(required = false) @Size(max = 20) String phone,
                         @RequestParam(required = false) @Size(max = 150) String course,
                         @RequestParam(required = false) @Size(max = 5000) String message,
                         RedirectAttributes redirect) {
        data.saveEnquiry(name, email, phone, course, message);
        redirect.addFlashAttribute("success", "Your enquiry has been received.");
        return "redirect:/contact";
    }
}
