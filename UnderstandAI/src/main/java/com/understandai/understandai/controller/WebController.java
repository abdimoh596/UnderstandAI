package com.understandai.understandai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @GetMapping("/")
    public String index(@RequestParam(value = "repo", required = false) String repo, 
                       HttpSession session, 
                       Model model) {
        // Check if user is authenticated with GitHub
        String githubToken = (String) session.getAttribute("GITHUB_TOKEN");
        model.addAttribute("isAuthenticated", githubToken != null);
        
        // If repo parameter is provided, pre-fill the form
        if (repo != null && !repo.isEmpty()) {
            model.addAttribute("prefilledRepo", repo);
        }
        
        return "frontend";
    }
} 