package com.understandai.understandai.Service;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.springframework.stereotype.Service;
import com.understandai.understandai.model.FileMetadata;

@Service
public class RepoScannerService {

    private static final Set<String> EXCLUDED_DIR_NAMES = Set.of(
        ".git", "node_modules", "vendor", "target",
        "build", ".idea", ".vscode", ".gradle", ".mvn", ".cache"
        );

    private static final Set<String> EXCLUDED_EXTENSIONS = Set.of(
        "log", "tmp", "swp", "swo", "bak", "old", "orig", "pyc", "pyo", "class", "exe", "dll", "so", "dylib",
        "jar", "war", "ear", "zip", "tar", "gz", "tgz", "bz2", "xz", "7z", "rar", "iso", "img", "apk", "ipa",
        "deb", "rpm", "msi", "cab", "dmg", "pkg", "app", "lock", "pid", "seed", "iml", "env", "gitignore", "metadata", "png"
    );

    public List<FileMetadata> scanRepo(Path repoPath) throws IOException {

        List<FileMetadata> fileMetadataList = new ArrayList<>();

        Files.walk(repoPath)
                .filter(Files::isRegularFile)
                .filter(this::shouldInclude)
                .filter(file -> file.toFile().length() <= 50 * 1024)
                .forEach(file -> {
                    if (!isUtf8(file)) {
                        return; // Skip non-UTF-8 files
                    }
                    FileMetadata metadata = new FileMetadata();
                    metadata.setFilePath(file.toString());
                    
                    try {
                        metadata.setFileType(Files.probeContentType(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    metadata.setFileSize(file.toFile().length());

                    try {
                        metadata.setContent(Files.readString(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("SOMETHING WENT WRONG HERE");
                        metadata.setContent("Error");
                    }

                    if (!metadata.getContent().equals("Error")) {
                        fileMetadataList.add(metadata);
                    }
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

        // Use content type to skip images, binaries, compressed files, etc.
        try {
            String contentType = Files.probeContentType(path);
            if (contentType != null) {
                if (
                    contentType.startsWith("image/") ||
                    contentType.startsWith("audio/") ||
                    contentType.startsWith("video/") ||
                    contentType.startsWith("text/xml") ||
                    contentType.equals("application/zip") ||
                    contentType.equals("application/x-executable") ||
                    contentType.equals("application/x-dosexec")
                ) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // If we can't determine the content type, we assume it's safe to include
            // This is a conservative approach to avoid excluding files unnecessarily
            return true;
        }

        return true;
    }

    private boolean isUtf8(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.decode(ByteBuffer.wrap(bytes));
            return true; // Decoding succeeded
        } catch (IOException e) {
            return false; // Not UTF-8 or unreadable
        }
    }

}