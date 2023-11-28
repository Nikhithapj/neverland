package com.example.admin.controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class AdminController {
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("title","Category");
        return "categories";
    }
}