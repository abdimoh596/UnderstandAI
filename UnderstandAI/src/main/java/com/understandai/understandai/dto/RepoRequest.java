package com.understandai.understandai.dto;
import lombok.Data;

@Data
public class RepoRequest {
    private String repoUrl;
    private String explanationLevel; // "basic", "intermediate", "advanced"
    private String analysisOutput; // "plainText" or "presentation"
}
