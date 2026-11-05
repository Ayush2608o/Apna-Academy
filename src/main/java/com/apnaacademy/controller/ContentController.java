package com.apnaacademy.controller;

import com.apnaacademy.service.AcademyDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    private final AcademyDataService data;
    public ContentController(AcademyDataService data) { this.data = data; }

    @GetMapping("/faculty")
    public String faculty(Model model) { return list(model, "Faculty", "faculty"); }
    @GetMapping("/events")
    public String events(Model model) { return list(model, "Events", "events"); }
    @GetMapping("/placements")
    public String placements(Model model) { return list(model, "Placement Stories", "placements"); }
    @GetMapping("/jobs")
    public String jobs(Model model) { return list(model, "Job Openings", "jobs"); }
    @GetMapping("/notes")
    public String notes(Model model) { return list(model, "Study Notes", "notes"); }

    private String list(Model model, String title, String table) {
        model.addAttribute("title", title);
        model.addAttribute("items", data.list(table));
        return "content/list";
    }
}
