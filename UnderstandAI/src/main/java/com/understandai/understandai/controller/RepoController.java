package com.understandai.understandai.controller;
import com.understandai.understandai.Service.GitCloneService;
import com.understandai.understandai.dto.RepoRequest;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RepoController {

    private GitCloneService gitCloneService;

    public RepoController(GitCloneService gitCloneService) {
        this.gitCloneService = gitCloneService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeRepo(@RequestBody RepoRequest repoRequest) throws IOException {
        String clonePath = gitCloneService.cloneRepo(repoRequest.getRepoUrl());

        return ResponseEntity.ok("Repository cloned successfully to: " + clonePath);
    }
}
