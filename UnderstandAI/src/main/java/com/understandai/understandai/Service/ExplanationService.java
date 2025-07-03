package com.understandai.understandai.Service;
import org.springframework.stereotype.Service;
import com.understandai.understandai.model.FileMetadata;

import java.util.ArrayList;
import java.util.List;


@Service
public class ExplanationService {

    private final OpenRouterService openRouterService;

    public ExplanationService(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    public String getExplanations(List<FileMetadata> metadataList, String repoName, String explanationLevel, String analysisOutput) {

        List<String> explanations = new ArrayList<>();

        StringBuilder promptBuilder = new StringBuilder();
        StringBuilder fileStringBuilder = new StringBuilder();

        for (FileMetadata metadata : metadataList) {
            fileStringBuilder.append("File Path: ").append(metadata.getFilePath()).append("\n");
            fileStringBuilder.append("File Type: ").append(metadata.getFileType()).append("\n");
            fileStringBuilder.append("File Size: ").append(metadata.getFileSize()).append(" bytes\n");
            fileStringBuilder.append("Content:\n").append(metadata.getContent()).append("\n\n");
        }
        fileStringBuilder.append("You can now respond. \n");

        int chars = fileStringBuilder.length();
        int totalParts = (chars / 15000) + 1;

        promptBuilder.append(defaultPrompt(repoName));

        promptBuilder.append(explanationLevelPrompt(explanationLevel));

        if (chars > 15000) {
            promptBuilder.append("The content of the files is too long to be processed in one go, so it will be split into parts.\n");
            promptBuilder.append("Please wait to respond until you get all of the parts, there are ").append(totalParts).append(" parts in total.\n");
            promptBuilder.append("Until you see the following text say 'You can now respond', only reply with 'okay'\n");
        } else {
            promptBuilder.append("The content of the files is as follows:\n");
        }

        if (analysisOutput.equalsIgnoreCase("presentation")) {
            promptBuilder.append(presentationModePrompt());
        } else {
            promptBuilder.append("And when you respond, please start whatever you are going to say with '----(name of the repo)----'\n");
            promptBuilder.append("And at any point in your response, do not ask the user to give follow ups. Your message is final.\n\n");
        }

        explanations.add(promptBuilder.toString());
        if (chars > 15000) {
            int parts = 1;
            for (int i = 0; i < chars; i += 15000) {
                String part = fileStringBuilder.toString().substring(i, Math.min(i + 15000, chars));
                explanations.add("Part " + parts + " of " + totalParts + ":\n" + part);
                parts++;
            }
        } else {
            explanations.add(fileStringBuilder.toString());
        }


        String aiExplanations = null;
        try {
            aiExplanations = openRouterService.getChunkedExplanations(explanations);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return aiExplanations;
    }

    private String defaultPrompt(String repoName) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an expert in code analysis and explanation. \n");
        promptBuilder.append("You will be given a list of files with their metadata, including file path, type, size, and content. \n");
        promptBuilder.append("I have parsed through the repo and excluded files which i believe are not important in describing functionality \n");
        promptBuilder.append("Your task is to provide a detailed explanation of the code in the repository, including its purpose, functionality, and  \n");
        promptBuilder.append("You will also provide a summary of the overall codebase, highlighting the main components, their interactions, and any notable patterns or practices. \n");
        promptBuilder.append("You can skip any files which you see to be unimportant which i was unable to exclude. \n");
        promptBuilder.append("Please provide an organized and structured response, using headings and bullet points where appropriate. \n");
        promptBuilder.append("The name of the repo you are analyzing is: " + repoName + "\n");

        return promptBuilder.toString();
    }

    private String explanationLevelPrompt(String level) {
        switch (level.toLowerCase()) {
            case "basic":
                return "The user has requested that you please provide your answer at a basic level explanation " +
                 "so along with previous instructions please explain this codebase to a complete beginner. Use plain language, no technical jargon, and analogies. " +
                 "Similar to explaining it to a non technical person.\n";

            case "intermediate":
                return "The user has requested that you please provide your answer at an intermediate level explanation " + 
                "so along with previous instructions please explain the code clearly to someone with basic programming experience. " +
                "Use simple technical language. Similar to explaining it to a college student or someone with some coding knowledge.\n";

            case "advanced":
                return "The user has requested that you please provide your answer at an advanced level explanation " +
                "so along with previous instructions please give a deep technical analysis of the code, including architecture," +
                " performance implications, and design patterns. Similar to explaining it to a senior developer or software architect.\n";
            default:
                return "\n";
        }
    }

    private String presentationModePrompt() {
        return """
        You are generating a PowerPoint presentation analysis of a GitHub repository.

        The output will be converted into a `.pptx` file using Apache POI.

        Respond with exactly 7 slides in this **strict format**:

        Each slide must begin with:
        === Slide N ===  
        Then include the fields `Title:` and `Content:` (or `Subtitle:` for Slide 1).  
        Follow this exact structure:

        === Slide 1 ===
        Title: [Title of the presentation]
        Subtitle: [Subtitle with repo name]

        === Slide 2 ===
        Title: Overview
        Content: [Brief overview of the codebase, its purpose, and main components]

        === Slide 3 ===
        Title: [Custom Title]
        Content: [Detailed explanation of a part of the repo]

        === Slide 4 ===
        Title: [Custom Title]
        Content: [Detailed explanation of a part of the repo]

        === Slide 5 ===
        Title: [Custom Title]
        Content: [Detailed explanation of a part of the repo]

        === Slide 6 ===
        Title: [Custom Title]
        Content: [Detailed explanation of a part of the repo]

        === Slide 7 ===
        Title: [Custom Title]
        Content: [Detailed explanation of a part of the repo]

        Use plain text. Do not include bullet points, markdown, or any formatting besides the structure above.
        Make sure all slides are present. Be strict with the formatting. If you dont follow the format above I will die
        """;
    }
}
