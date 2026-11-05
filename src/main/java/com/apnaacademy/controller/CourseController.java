package com.apnaacademy.controller;

import com.apnaacademy.service.AcademyDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CourseController {
    private final AcademyDataService data;
    public CourseController(AcademyDataService data) { this.data = data; }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", data.list("courses"));
        return "courses";
    }

    @GetMapping("/courses/{id}")
    public String course(@PathVariable long id, Model model) {
        model.addAttribute("course", data.find("courses", id));
        return "course-detail";
    }
}
