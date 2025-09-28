package uy.com.fing.hicscan.hceanalysis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.data.Recommendation.WomanRecommendationService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hicscan-api/testing/woman-recommendation")
public class TestingWomanRecommendationController {

    private final WomanRecommendationService womanRecommendationService;

    public TestingWomanRecommendationController(WomanRecommendationService womanRecommendationService) {
        this.womanRecommendationService = womanRecommendationService;
    }

    /**
     * Endpoint to get woman recommendation
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> getWomanRecommendation(@RequestBody Map<String, Object> womanHistory) {
        try {
            Map<String, Object> response = womanRecommendationService.getWomanRecommendation(womanHistory);
            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error getting woman recommendation: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
