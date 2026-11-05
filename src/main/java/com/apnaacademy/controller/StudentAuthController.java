package com.apnaacademy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentAuthController {
    @GetMapping("/login")
    public String login() { return "login"; }
}
