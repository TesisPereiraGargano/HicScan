package uy.com.fing.hicscan.hceanalysis.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreenClient;

import java.io.IOException;

@RestController
@RequestMapping("/hicscan-api/testing")
public class TestingController {

    private final OntoBreastScreenClient ontoBreastScreenClient;

    public TestingController() {
        this.ontoBreastScreenClient = new OntoBreastScreenClient();
    }

    @GetMapping("/recommendation")
    public ResponseEntity<String> getBreastCancerRecommendation(
            @RequestParam String womanId,
            @RequestParam String guidelineUri) {
        try {
            String response = ontoBreastScreenClient.getBreastCancerRecommendation(womanId, guidelineUri);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().body("Error getting recommendation: " + e.getMessage());
        }
    }

    @GetMapping("/recommendation-guide")
    public ResponseEntity<String> getRecommendationGuideUri(
            @RequestParam String riskLevel) {
        try {
            String response = ontoBreastScreenClient.getRecommendationGuideUri(riskLevel);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().body("Error getting recommendation guide: " + e.getMessage());
        }
    }
} 