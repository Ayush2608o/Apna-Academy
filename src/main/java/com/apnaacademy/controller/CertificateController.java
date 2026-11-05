package com.apnaacademy.controller;

import com.apnaacademy.service.AcademyDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CertificateController {
    private final AcademyDataService data;
    public CertificateController(AcademyDataService data) { this.data = data; }

    @GetMapping("/certificate/verify")
    public String form() { return "certificate-lookup"; }

    @PostMapping("/certificate/verify")
    public String verify(@RequestParam String certificateId, Model model) {
        boolean hasCertificates = data.hasCertificates();
        model.addAttribute("certificateId", certificateId.trim());
        model.addAttribute("unavailable", !hasCertificates);
        model.addAttribute("matches", hasCertificates ? data.verifyCertificate(certificateId.trim()) : java.util.List.of());
        return "certificate-lookup";
    }
}
