package uy.com.fing.hicscan.hceanalysis.data.OntoForms;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class OntoForms {
    
    private static final String UPLOAD_FILENAME = "Breast_cancer_recommendation_drugs_08_04_2025.rdf";
    private static final String ONTOFORMS_BASE_URL = "http://localhost:8081/ontoforms-api/v1/";
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
    
    private String executePostRequest(String url, String contentType, byte[] body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
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
    
    public String uploadOntology(File file) throws IOException, InterruptedException {
        // Create multipart boundary
        String boundary = "----WebFormBoundary" + System.currentTimeMillis();
        
        // Read file content
        byte[] fileContent = Files.readAllBytes(file.toPath());
        
        // Create multipart body
        String encodedFilename = URLEncoder.encode(UPLOAD_FILENAME, StandardCharsets.UTF_8);
        String multipartBody = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + encodedFilename + "\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n";
        
        byte[] beforeFile = multipartBody.getBytes(StandardCharsets.UTF_8);
        byte[] afterFile = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
        
        // Combine all parts
        byte[] fullBody = new byte[beforeFile.length + fileContent.length + afterFile.length];
        System.arraycopy(beforeFile, 0, fullBody, 0, beforeFile.length);
        System.arraycopy(fileContent, 0, fullBody, beforeFile.length, fileContent.length);
        System.arraycopy(afterFile, 0, fullBody, beforeFile.length + fileContent.length, afterFile.length);
        
        String responseBody = executePostRequest(
            ONTOFORMS_BASE_URL + "ontologies",
            "multipart/form-data; boundary=" + boundary,
            fullBody
        );
        
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            JsonNode idNode = root.get("id");
            if (idNode == null || !idNode.isTextual()) {
                throw new IOException("Invalid response format: missing or invalid 'id' field");
            }
            String ontologyUrl = idNode.asText();
            if (ontologyUrl.isEmpty()) {
                throw new IOException("Invalid response format: empty URL received");
            }
            return ontologyUrl;
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }
}
