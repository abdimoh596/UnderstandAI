package com.understandai.understandai.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.springframework.stereotype.Service;
import com.understandai.understandai.model.FileMetadata;

@Service
public class RepoScannerService {

    private static final Set<String> EXCLUDED_DIR_NAMES = Set.of(
        ".git", "node_modules", "vendor", "target",
        "build", ".idea", ".vscode", ".gradle", ".mvn", ".cache", "Temp"
        );

    private static final Set<String> EXCLUDED_EXTENSIONS = Set.of(
        "log", "tmp", "swp", "swo", "bak", "old", "orig", "pyc", "pyo", "class", "exe", "dll", "so", "dylib",
        "jar", "war", "ear", "zip", "tar", "gz", "tgz", "bz2", "xz", "7z", "rar", "iso", "img", "apk", "ipa",
        "deb", "rpm", "msi", "cab", "dmg", "pkg", "app", "lock", "pid", "seed", "iml"
    );

    public List<FileMetadata> scanRepo(Path repoPath) throws IOException {

        List<FileMetadata> fileMetadataList = new ArrayList<>();

        Files.walk(repoPath)
                .filter(Files::isRegularFile)
                .filter(this::shouldInclude)
                .forEach(file -> {
                    FileMetadata metadata = new FileMetadata();
                    metadata.setFilePath(file.toString());
                    try {
                        metadata.setFileType(Files.probeContentType(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    metadata.setFileSize(file.toFile().length());
                    fileMetadataList.add(metadata);
                });

        return fileMetadataList;
    }

    private boolean shouldInclude(Path path) {
        // Check each part of the path for excluded directory names
        for (Path part : path) {
            if (EXCLUDED_DIR_NAMES.contains(part.toString().toLowerCase())) {
                return false;
            }
        }

        // Check extension
        String fileName = path.getFileName().toString().toLowerCase();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            String extension = fileName.substring(lastDotIndex + 1);
            if (EXCLUDED_EXTENSIONS.contains(extension)) {
                return false;
            }
        }

        return true;
    }

}