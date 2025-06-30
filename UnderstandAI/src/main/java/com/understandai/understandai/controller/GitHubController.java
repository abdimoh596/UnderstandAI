package com.understandai.understandai.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class GitHubController {
    
    Dotenv dotenv = Dotenv.load();

    private String clientId = dotenv.get("GITHUBID");

    private String clientSecret = dotenv.get("GITHUBSECRET");

    private final String redirectUri = "http://localhost:8080/callback";

    @GetMapping("/login/github")
    public void githubLogin(HttpServletResponse response) throws IOException {
        String oauthUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=repo%20read:user";

        response.sendRedirect(oauthUrl);
    }

    @GetMapping("/callback")
    public String githubCallback(@RequestParam("code") String code, HttpSession session) throws IOException {
        // exchange code for access token
        String accessToken = fetchAccessToken(code);
        session.setAttribute("GITHUB_TOKEN", accessToken);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private String fetchAccessToken(String code) throws IOException {
        HttpClient client = HttpClient.newHttpClient();

        String body = "client_id=" + clientId +
                      "&client_secret=" + clientSecret +
                      "&code=" + code;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://github.com/login/oauth/access_token"))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject(response.body());
        return json.getString("access_token");
    }

    @GetMapping("/repos")
    public String listRepos(HttpSession session, Model model) throws IOException {
        String token = (String) session.getAttribute("GITHUB_TOKEN");
        if (token == null) return "redirect:/";

        GitHub github = new GitHubBuilder().withOAuthToken(token).build();
        GHMyself user = github.getMyself();

        List<String> repoNames = new ArrayList<>();
        for (GHRepository repo : user.listRepositories()) {
            repoNames.add(repo.getFullName());
        }

        model.addAttribute("repos", repoNames);
        return "repos"; // This will load a Thymeleaf HTML template
    }
}
