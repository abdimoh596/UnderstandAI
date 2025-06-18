package com.understandai.understandai.controller;
import com.understandai.understandai.dto.RepoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RepoController {
    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeRepo(@RequestBody RepoRequest repoRequest) {
        // Here you would typically handle the request, e.g., save the repo URL to a database
        String repoUrl = repoRequest.getRepoUrl();
        System.out.println("Received repo URL: " + repoUrl);
        // For demonstration, we just return the received URL
        return ResponseEntity.ok("Received repo URL: " + repoUrl);
    }
}
