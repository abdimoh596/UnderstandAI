package com.understandai.understandai.Service;
import org.springframework.stereotype.Service;
import com.understandai.understandai.model.FileMetadata;
import java.util.List;


@Service
public class ExplanationService {

    public String getExplanations(List<FileMetadata> metadataList, String repoName) {

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an expert in code analysis and explanation. \n");
        promptBuilder.append("You will be given a list of files with their metadata, including file path, type, size, and content. \n");
        promptBuilder.append("Your task is to provide a detailed explanation of the code in each file, including its purpose, functionality, and any potential issues or improvements. \n");
        promptBuilder.append("You will also provide a summary of the overall codebase, highlighting the main components, their interactions, and any notable patterns or practices. \n");
        promptBuilder.append("Please provide an organized and structured response, using headings and bullet points where appropriate. \n");
        promptBuilder.append("The name of the repo you are analyzing is: " + repoName + "\n\n");

        for (FileMetadata metadata : metadataList) {
            promptBuilder.append("File Path: ").append(metadata.getFilePath()).append("\n");
            promptBuilder.append("File Type: ").append(metadata.getFileType()).append("\n");
            promptBuilder.append("File Size: ").append(metadata.getFileSize()).append(" bytes\n");
            promptBuilder.append("Content:\n").append(metadata.getContent()).append("\n\n");
        }

        return promptBuilder.toString();
    }
}
