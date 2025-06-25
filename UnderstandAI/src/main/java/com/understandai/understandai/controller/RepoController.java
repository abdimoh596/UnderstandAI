package com.understandai.understandai.controller;
import com.understandai.understandai.Service.GitCloneService;
import com.understandai.understandai.Service.RepoScannerService;
import com.understandai.understandai.dto.RepoRequest;
import com.understandai.understandai.model.FileMetadata;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RepoController {

    private GitCloneService gitCloneService;
    private RepoScannerService repoScannerService;

    public RepoController(GitCloneService gitCloneService, RepoScannerService repoScannerService) {
        this.gitCloneService = gitCloneService;
        this.repoScannerService = repoScannerService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeRepo(@RequestBody RepoRequest repoRequest) throws IOException {
        Path clonePath = gitCloneService.cloneRepo(repoRequest.getRepoUrl());

        for (FileMetadata fileMetadata : repoScannerService.scanRepo(clonePath)) {
            System.out.println("File Path: " + fileMetadata.getFilePath());
            System.out.println("File Type: " + fileMetadata.getFileType());
            System.out.println("File Size: " + fileMetadata.getFileSize());
            // print the size of the list of filemetadata objects
            System.out.println("Total files scanned: " + repoScannerService.scanRepo(clonePath).size());
        }
        return ResponseEntity.ok("Repository cloned successfully to: " + clonePath);
    }
}
