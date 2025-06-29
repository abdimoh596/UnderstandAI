package com.understandai.understandai.Service;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitCloneService {

    public Path cloneRepo(String repoUrl) throws IOException {
        Path path = Files.createTempDirectory("repo-clone-");

        try {
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(path.toFile())
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return path.resolve("Error cloning repository: " + e.getMessage());
        }
        
        return path;
    }

}
