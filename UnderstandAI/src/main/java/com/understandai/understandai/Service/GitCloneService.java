package com.understandai.understandai.Service;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitCloneService {

    public Path cloneRepo(String repoUrl, String token) throws IOException {
        Path path = Files.createTempDirectory("repo-clone-");

        try {
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(path.toFile())
                    .setCredentialsProvider(
                        (token != null && !token.isEmpty())
                            ? new UsernamePasswordCredentialsProvider("oauth2", token)
                            : null
                    )
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            return path.resolve("Error cloning repository: " + e.getMessage());
        }
        return path;
    }

}
