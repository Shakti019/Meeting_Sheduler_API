package com.App.live.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/schedule")
    public String schedule() {
        return "schedule";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model) {
        model.addAttribute("meetingId", id);
        return "edit";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
