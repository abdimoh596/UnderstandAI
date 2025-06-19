package com.understandai.understandai.Service;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitCloneService {

    public String cloneRepo(String repoUrl) throws IOException {
        Path path = Files.createTempDirectory("repo-clone-");
        System.out.println("Cloning repository to: " + path.toAbsolutePath());

        try {
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(path.toFile())
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return "Error cloning repository: " + e.getMessage();
        }

        return path.toString();
    }

}
