package com.example.admin.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
//
//    @RequestMapping("/index")
//    public String home() {
//        return "index";
//    }
}
