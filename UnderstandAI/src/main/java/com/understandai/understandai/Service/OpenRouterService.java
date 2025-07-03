package com.understandai.understandai.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.http.HttpRequest;
import org.springframework.stereotype.Service;


@Service
public class OpenRouterService {

    private final String openRouterKey;
    private final HttpClient httpClient;

    public OpenRouterService() {
        openRouterKey = System.getenv("OPENROUTER_API_KEY");
        if (openRouterKey == null || openRouterKey.isEmpty()) {
            throw new RuntimeException("OPENROUTER_API_KEY environment variable is not set");
        }
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

        String payloadStr = body.toString();
        int charLimit = 500_000;  // Safe threshold for context
        if (payloadStr.length() > charLimit) {
            body.put("transforms", new JSONArray().put("middle-out"));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + openRouterKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());

        if (jsonResponse.has("error")) {
            JSONObject error = jsonResponse.getJSONObject("error");
            int errorCode = error.optInt("code");
            String message = error.optString("message");

            throw new RuntimeException("OpenRouter API Error " + errorCode + ": " + message);
        }

        if (!jsonResponse.has("choices")) {
            throw new RuntimeException("Missing 'choices' in OpenRouter response");
        }

        JSONArray choices = jsonResponse.getJSONArray("choices");

        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }

}
