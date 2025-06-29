package com.understandai.understandai.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.http.HttpRequest;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class OpenRouterService {

    private final String openRouterKey;
    private final HttpClient httpClient;

    public OpenRouterService() {
        Dotenv dotenv = Dotenv.load();
        this.openRouterKey = dotenv.get("OPENROUTERKEY");
        this.httpClient = HttpClient.newHttpClient();
    }

     public String getChunkedExplanations(List<String> promptChunks) throws Exception {
        URI uri = URI.create("https://openrouter.ai/api/v1/chat/completions");

        JSONArray messages = new JSONArray();

        // Add each prompt chunk as its own message
        for (int i = 0; i < promptChunks.size(); i++) {
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", promptChunks.get(i));
            messages.put(userMsg);
        }

        JSONObject body = new JSONObject();
        body.put("model", "deepseek/deepseek-r1-0528-qwen3-8b:free"); // or another OpenRouter-supported model
        body.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + openRouterKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        JSONArray choices = json.getJSONArray("choices");
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }

}
