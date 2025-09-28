package uy.com.fing.hicscan.hceanalysis.data.Recommendation;

import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreenClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of WomanRecommendationService
 * Orchestrates the process of getting woman recommendations by calling multiple services in sequence
 */
@Service
public class WomanRecommendationServiceImpl implements WomanRecommendationService {
    
    @Autowired
    private OntoBreastScreenClient ontoBreastScreenClient;
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Override
    public Map<String, Object> getWomanRecommendation(Map<String, Object> womanHistory) throws IOException, InterruptedException {
        try {
            // Step 1: Create woman and calculate risk
            OntoBreastScreenClient.WomanRiskResult womanRiskResult = ontoBreastScreenClient.createWomanAndCalculateRisk(womanHistory);
            String womanId = womanRiskResult.womanUri;
            String riskLevelUri = womanRiskResult.riskLevelUri;
            
            // Step 2: Get recommendation guide URI based on risk level
            String recommendationGuideResponse = ontoBreastScreenClient.getRecommendationGuideUri(riskLevelUri);
            
            // Extract the guideline URI from the response
            String guidelineUri = extractGuidelineUri(recommendationGuideResponse);
            
            // Step 3: Get breast cancer recommendation
            Map<String, Object> recommendation = ontoBreastScreenClient.getBreastCancerRecommendation(womanId, guidelineUri);
            
            // Add womanId to the response
            Map<String, Object> response = new HashMap<>(recommendation);
            response.put("womanId", womanId);
            
            return response;
            
        } catch (IOException | InterruptedException e) {
            throw new IOException("Error getting woman recommendation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extracts the guideline URI from the recommendation guide response
     * 
     * @param recommendationGuideResponse JSON response containing the guideline URI
     * @return String containing the extracted guideline URI
     * @throws IOException if the response cannot be parsed
     */
    private String extractGuidelineUri(String recommendationGuideResponse) throws IOException {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(recommendationGuideResponse);
            JsonNode uriNode = root.get("uri");
            
            if (uriNode == null || !uriNode.isTextual()) {
                throw new IOException("Invalid response format: missing or invalid 'uri' field");
            }
            
            String uri = uriNode.asText();
            if (uri.isEmpty()) {
                throw new IOException("Invalid response format: empty URI received");
            }
            
            return uri;
        } catch (IOException e) {
            throw new IOException("Failed to parse recommendation guide response: " + e.getMessage(), e);
        }
    }
}
