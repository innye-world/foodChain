package com.inhye.foodChain.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "대시보드");
        return "dashboard";
    }
}