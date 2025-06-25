package com.understandai.understandai.model;
import lombok.Data;

@Data
public class FileMetadata {
    private String filePath;
    private String fileType;
    private long fileSize;
}
