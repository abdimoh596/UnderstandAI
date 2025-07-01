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

    public String getExplanations(List<FileMetadata> metadataList, String repoName, String explanationLevel) {

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

        promptBuilder.append("You are an expert in code analysis and explanation. \n");
        promptBuilder.append("You will be given a list of files with their metadata, including file path, type, size, and content. \n");
        promptBuilder.append("I have parsed through the repo and excluded files which i believe are not important in describing functionality \n");
        promptBuilder.append("Your task is to provide a detailed explanation of the code in the repository, including its purpose, functionality, and  \n");
        promptBuilder.append("You will also provide a summary of the overall codebase, highlighting the main components, their interactions, and any notable patterns or practices. \n");
        promptBuilder.append("You can skip any files which you see to be unimportant which i was unable to exclude. \n");
        promptBuilder.append("Please provide an organized and structured response, using headings and bullet points where appropriate. \n");
        promptBuilder.append("The name of the repo you are analyzing is: " + repoName + "\n");

        promptBuilder.append(explanationLevelPrompt(explanationLevel));

        if (chars > 15000) {
            promptBuilder.append("The content of the files is too long to be processed in one go, so it will be split into parts.\n");
            promptBuilder.append("Please wait to respond until you get all of the parts, there are ").append(totalParts).append(" parts in total.\n");
            promptBuilder.append("Until you see the following text say 'You can now respond', only reply with 'okay'\n");
        } else {
            promptBuilder.append("The content of the files is as follows:\n");
        }

        promptBuilder.append("And when you respond, please start whatever you are going to say with '----(name of the repo)----'\n");
        promptBuilder.append("And at any point in your response, do not ask the user to give follow ups. Your message is final.\n\n");

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
}
