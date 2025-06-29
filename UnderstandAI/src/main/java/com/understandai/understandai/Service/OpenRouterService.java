package com.understandai.understandai.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
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

     public List<String> getChunkedExplanations(List<String> promptChunks) {
        List<String> responses = new ArrayList<>();

        for (String chunk : promptChunks) {
            try {
                String response = sendPromptChunk(chunk);
                responses.add(response);
                // Optional: delay slightly between calls to avoid rate limits
                Thread.sleep(2000); 
            } catch (Exception e) {
                e.printStackTrace();
                responses.add("Error in chunk: " + e.getMessage());
            }
        }

        return responses;
    }

    private String sendPromptChunk(String promptChunk) throws Exception {
        URI uri = URI.create("https://openrouter.ai/api/v1/chat/completions");

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", promptChunk);

        JSONArray messages = new JSONArray();
        messages.put(message);

        JSONObject body = new JSONObject();
        body.put("model", "google/gemma-3n-e4b-it:free"); // or another OpenRouter-supported model
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
