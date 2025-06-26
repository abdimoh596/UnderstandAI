package com.understandai.understandai.controller;
import com.understandai.understandai.Service.GitCloneService;
import com.understandai.understandai.Service.RepoScannerService;
import com.understandai.understandai.dto.RepoRequest;
import com.understandai.understandai.model.FileMetadata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.understandai.understandai.Service.ExplanationService;

@RestController
@RequestMapping("/api")
public class RepoController {

    private GitCloneService gitCloneService;
    private RepoScannerService repoScannerService;
    private ExplanationService explanationService;

    public RepoController(GitCloneService gitCloneService, RepoScannerService repoScannerService, 
                          ExplanationService explanationService) {
        this.gitCloneService = gitCloneService;
        this.repoScannerService = repoScannerService;
        this.explanationService = explanationService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> explainRepo(@RequestBody RepoRequest repoRequest) throws IOException {
        
        String url = repoRequest.getRepoUrl();
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        String repoName = url.substring(url.lastIndexOf("/") + 1);

        Path clonePath = gitCloneService.cloneRepo(repoRequest.getRepoUrl());

        List<FileMetadata> metadataList = repoScannerService.scanRepo(clonePath);

        if (metadataList.isEmpty()) {
            return ResponseEntity.ok("No files found in the repository.");
        }

        String explanationPrompt = explanationService.getExplanations(metadataList, repoName);

        return ResponseEntity.ok(explanationPrompt);
    }
}
