package com.understandai.understandai.controller;
import com.understandai.understandai.Service.GitCloneService;
import com.understandai.understandai.Service.RepoScannerService;
import com.understandai.understandai.dto.RepoRequest;
import com.understandai.understandai.model.FileMetadata;
import com.understandai.understandai.model.PowerpointSlide;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.understandai.understandai.Service.ExplanationService;
import jakarta.servlet.http.HttpSession;
import com.understandai.understandai.Service.PowerpointService;

@RestController
@RequestMapping("/api")
public class RepoController {

    private GitCloneService gitCloneService;
    private RepoScannerService repoScannerService;
    private ExplanationService explanationService;
    private PowerpointService powerpointService;

    public RepoController(GitCloneService gitCloneService, RepoScannerService repoScannerService, 
                          ExplanationService explanationService, PowerpointService powerpointService) {
        this.gitCloneService = gitCloneService;
        this.repoScannerService = repoScannerService;
        this.explanationService = explanationService;
        this.powerpointService = powerpointService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> explainRepo(@RequestBody RepoRequest repoRequest, HttpSession session) throws IOException {
        
        String url = repoRequest.getRepoUrl();
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body("Repository URL is required.");
        }

        String level = repoRequest.getExplanationLevel();
        if (level == null || level.isEmpty()) {
            return ResponseEntity.badRequest().body("Explanation level is required.");
        }
        
        String analysisOutput = repoRequest.getAnalysisOutput();
        if (analysisOutput == null || analysisOutput.isEmpty()) {
            return ResponseEntity.badRequest().body("Analysis output type is required.");
        }

        String repoName = url.substring(url.lastIndexOf("/") + 1);

        // Get token from session if available
        String token = (String) session.getAttribute("GITHUB_TOKEN");

        Path clonePath = gitCloneService.cloneRepo(repoRequest.getRepoUrl(), token);

        List<FileMetadata> metadataList = repoScannerService.scanRepo(clonePath);

        if (metadataList.isEmpty()) {
            return ResponseEntity.ok("No files found in the repository.");
        }

        String aiResponse = explanationService.getExplanations(metadataList, repoName, level, analysisOutput);

        if ("presentation".equalsIgnoreCase(analysisOutput)) {
            // Parse slides for preview
            List<PowerpointSlide> slides = powerpointService.parseSlides(aiResponse);
            Map<String, Object> response = new HashMap<>();
            response.put("type", "presentation");
            response.put("slides", slides);
            response.put("aiResponse", aiResponse);
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "plaintext");
            response.put("content", aiResponse);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/download-pptx")
    public ResponseEntity<?> downloadPptx(@RequestBody Map<String, String> body) throws IOException {
        String aiResponse = body.get("aiResponse");
        if (aiResponse == null || aiResponse.isEmpty()) {
            return ResponseEntity.badRequest().body("AI response is required to generate PowerPoint.");
        }
        byte[] pptxBytes = powerpointService.createPresentationFromAnalysis(aiResponse);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=repo_analysis.pptx")
                .body(pptxBytes);
    }
}
