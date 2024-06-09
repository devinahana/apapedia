package com.apapedia.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("test", "This is user");
        return "home";
    }
}
