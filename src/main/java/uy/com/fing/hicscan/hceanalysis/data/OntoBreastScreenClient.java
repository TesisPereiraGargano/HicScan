package uy.com.fing.hicscan.hceanalysis.data;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class OntoBreastScreenClient {
    
    private static final String RECOMMENDATION_BASE_URL = "http://localhost:8080/breast-cancer-recommendation-api/v1/";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    private String executeGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Log the response status
        System.out.println("Response status: " + response.statusCode());
        
        // Check if the request was successful
        if (response.statusCode() != 200) {
            throw new IOException("HTTP request failed with status code: " + response.statusCode() + 
                                ", response: " + response.body());
        }
        
        return response.body();
    }
    
    public String getBreastCancerRecommendation(String womanId, String guidelineUri) throws IOException, InterruptedException {
        String encodedWomanId = URLEncoder.encode(womanId, StandardCharsets.UTF_8);
        String encodedGuidelineUri = URLEncoder.encode(guidelineUri, StandardCharsets.UTF_8);
        String url = RECOMMENDATION_BASE_URL + "recommendation?womanId=" + 
                     encodedWomanId + "&guidelineUri=" + encodedGuidelineUri;
        
        return executeGetRequest(url);
    }
    
    public RecommendationResult processRecommendationResponse(String responseBody) throws IOException {
        // Handle empty response (no recommendation)
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new RecommendationResult(false, new HashMap<>());
        }
        
        String trimmedResponse = responseBody.trim();
        
        // Handle empty JSON response
        if (trimmedResponse.equals("{}") || trimmedResponse.equals("")) {
            return new RecommendationResult(false, new HashMap<>());
        }
        
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            boolean isHigh = false;
            Map<String, String> properties = new HashMap<>();
            
            JsonNode highRecommendation = root.get("highRecommendation");
            JsonNode midRecommendation = root.get("midRecommendation");
            
            if (highRecommendation != null && !highRecommendation.isNull()) {
                isHigh = true;
                extractJsonProperties(highRecommendation, properties);
            } else if (midRecommendation != null && !midRecommendation.isNull()) {
                isHigh = false;
                extractJsonProperties(midRecommendation, properties);
            }
            
            return new RecommendationResult(isHigh, properties);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }
    
    private void extractJsonProperties(JsonNode node, Map<String, String> properties) {
        node.fields().forEachRemaining(entry -> {
            if (entry.getValue().isTextual()) {
                properties.put(entry.getKey(), entry.getValue().asText());
            }
        });
    }
    
    public String getRecommendationGuideUri(String riskLevel) throws IOException, InterruptedException {
        String encodedRiskValue = URLEncoder.encode(riskLevel, StandardCharsets.UTF_8);
        String url = RECOMMENDATION_BASE_URL + "recommendation_guides?risk=" + encodedRiskValue;
        
        String responseBody = executeGetRequest(url);
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            if (!root.isArray() || root.size() == 0) {
                throw new IOException("Invalid response format: expected non-empty JSON array");
            }
            
            JsonNode firstObject = root.get(0);
            JsonNode uriNode = firstObject.get("uri");
            if (uriNode == null || !uriNode.isTextual()) {
                throw new IOException("Invalid response format: missing or invalid 'uri' field");
            }
            
            String uri = uriNode.asText();
            if (uri.isEmpty()) {
                throw new IOException("Invalid response format: empty URI received");
            }
            
            // Create a JSON object with the uri property
            Map<String, String> uriObject = new HashMap<>();
            uriObject.put("uri", uri);
            
            return OBJECT_MAPPER.writeValueAsString(uriObject);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }
    
    // Inner class to represent the result
    public static class RecommendationResult {
        public final boolean isHigh;
        public final Map<String, String> properties;
        
        public RecommendationResult(boolean isHigh, Map<String, String> properties) {
            this.isHigh = isHigh;
            this.properties = properties;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{isHigh: ").append(isHigh);
            
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                sb.append(", ").append(entry.getKey()).append(": \"").append(entry.getValue()).append("\"");
            }
            
            sb.append("}");
            return sb.toString();
        }
        
        public String getProperty(String name) {
            return properties.get(name);
        }
    }
}
