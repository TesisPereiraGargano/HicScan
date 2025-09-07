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
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.OntoTree;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.PropertyDescriptor;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.IndividualDescriptor;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.CalculatedPropertyConfigDescriptor;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.ArtificeClassConfigDescriptor;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.Form;

@Component
public class OntoForms {
    
    @Value("${ontoforms.client.url}")
    private String ontoformsBaseUrl;
    
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
    
    public String[] getOntologyNames() throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies";
        String responseBody = executeGetRequest(url);
        
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            
            if (!root.isArray()) {
                throw new IOException("Invalid response format: expected JSON array");
            }
            
            List<String> names = new ArrayList<>();
            for (JsonNode ontologyNode : root) {
                JsonNode nameNode = ontologyNode.get("name");
                if (nameNode != null && nameNode.isTextual()) {
                    names.add(nameNode.asText());
                }
            }
            
            return names.toArray(new String[0]);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }
    
    public void postAppMapping(String ontologyName, String appName) throws IOException, InterruptedException {
        if (ontologyName == null || ontologyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ontology name cannot be null or empty");
        }
        
        if (appName == null || appName.trim().isEmpty()) {
            throw new IllegalArgumentException("App name cannot be null or empty");
        }
        
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontologyName, StandardCharsets.UTF_8) + "/configurations/app-mapping";
        
        // Create JSON body with appName
        String jsonBody = "{\"appName\": \"" + appName.replace("\"", "\\\"") + "\"}";
        byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        
        // Execute POST request - any errors will be thrown by executePostRequest
        executePostRequest(url, "application/json", bodyBytes);
    }
    
    public String uploadOntology(File file) throws IOException, InterruptedException {
        // Create multipart boundary
        String boundary = "----WebFormBoundary" + System.currentTimeMillis();
        
        // Read file content
        byte[] fileContent = Files.readAllBytes(file.toPath());
        
        // Create multipart body
        String encodedFilename = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
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
            ontoformsBaseUrl + "ontologies",
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

    public OntoTree getOntologyClasses(String ontoId) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/classes";
        String responseBody = executeGetRequest(url);
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        try {
            return OBJECT_MAPPER.readValue(responseBody, OntoTree.class);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }

    public List<PropertyDescriptor> getOntologyClassProperties(String ontoId, String classUri) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/properties?domainClassUri=" + URLEncoder.encode(classUri, StandardCharsets.UTF_8);
        String responseBody = executeGetRequest(url);
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        try {
            PropertyDescriptor[] array = OBJECT_MAPPER.readValue(responseBody, PropertyDescriptor[].class);
            return java.util.Arrays.asList(array);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }

    public List<IndividualDescriptor> getOntologyClassIndividuals(String ontoId, String classUri) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/individuals?classUri=" + URLEncoder.encode(classUri, StandardCharsets.UTF_8);
        String responseBody = executeGetRequest(url);
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        try {
            IndividualDescriptor[] array = OBJECT_MAPPER.readValue(responseBody, IndividualDescriptor[].class);
            return java.util.Arrays.asList(array);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }

    public List<CalculatedPropertyConfigDescriptor> getOntologyCalculatedPropertiesConfig(String ontoId) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/configurations/calculated-properties";
        String responseBody = executeGetRequest(url);
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        try {
            CalculatedPropertyConfigDescriptor[] array = OBJECT_MAPPER.readValue(responseBody, CalculatedPropertyConfigDescriptor[].class);
            return java.util.Arrays.asList(array);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }

    public void postOntologyCalculatedPropertyConfig(String ontoId, CalculatedPropertyConfigDescriptor calculatedPropertyConfigDescriptor) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/configurations/calculated-properties";
        byte[] bodyBytes = OBJECT_MAPPER.writeValueAsBytes(calculatedPropertyConfigDescriptor);
        executePostRequest(url, "application/json", bodyBytes);
    }

    public void deleteOntologyCalculatedPropertyConfig(String ontoId, CalculatedPropertyConfigDescriptor calculatedPropertyConfigDescriptor) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/configurations/calculated-properties";
        byte[] bodyBytes = OBJECT_MAPPER.writeValueAsBytes(calculatedPropertyConfigDescriptor);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response status: " + response.statusCode());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP DELETE request failed with status code: " + response.statusCode() + ", response: " + response.body());
        }
    }

    public List<ArtificeClassConfigDescriptor> getOntologyArtificeClassesConfig(String ontoId) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/configurations/artifice-classes";
        String responseBody = executeGetRequest(url);
        if (responseBody == null || responseBody.trim().isEmpty()) {
            throw new IOException("Empty response body received from server");
        }
        try {
            ArtificeClassConfigDescriptor[] array = OBJECT_MAPPER.readValue(responseBody, ArtificeClassConfigDescriptor[].class);
            return java.util.Arrays.asList(array);
        } catch (IOException e) {
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }

    public void postOntologyArtificeClassConfig(String ontoId, ArtificeClassConfigDescriptor artificeClassConfigDescriptor) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/configurations/artifice-classes";
        byte[] bodyBytes = OBJECT_MAPPER.writeValueAsBytes(artificeClassConfigDescriptor);
        executePostRequest(url, "application/json", bodyBytes);
    }

    public void deleteOntologyArtificeClassConfig(String ontoId, ArtificeClassConfigDescriptor artificeClassConfigDescriptor) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/configurations/artifice-classes";
        byte[] bodyBytes = OBJECT_MAPPER.writeValueAsBytes(artificeClassConfigDescriptor);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response status: " + response.statusCode());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP DELETE request failed with status code: " + response.statusCode() + ", response: " + response.body());
        }
    }

    public Form getOntologyClassForm(String ontoId, String classUri) throws IOException, InterruptedException {
        String url = ontoformsBaseUrl + "ontologies/" + URLEncoder.encode(ontoId, StandardCharsets.UTF_8) + "/forms?classUri=" + URLEncoder.encode(classUri, StandardCharsets.UTF_8);
        System.out.println("URL: " + url);
        String responseBody = executeGetRequest(url);
        if (responseBody == null || responseBody.trim().isEmpty()) {
            // Return an empty Form instead of throwing an error when no form exists
            System.out.println("No form found for class " + classUri + ", returning empty Form");
            return new Form(classUri, null);
        }
        System.out.println("Response body: " + responseBody);
        try {
            return OBJECT_MAPPER.readValue(responseBody, Form.class);
        } catch (IOException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            System.err.println("Response body that failed to parse: " + responseBody);
            throw new IOException("Failed to parse response JSON: " + e.getMessage(), e);
        }
    }
}
