package uy.com.fing.hicscan.hceanalysis.data.Recommendation;

import uy.com.fing.hicscan.hceanalysis.usecases.BreastCancerStudiesUseCase;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.WomanRisk;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.recommend.WomanRecommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of WomanRecommendationService
 * Orchestrates the process of getting woman recommendations by calling multiple services in sequence
 */
@Service
public class WomanRecommendationServiceImpl implements WomanRecommendationService {
    
    private static final String DEFAULT_RISK_MODEL_URI = "http://purl.org/ontology/breast_cancer_recommendation#UY_model";
    
    @Autowired
    private BreastCancerStudiesUseCase breastCancerStudiesUseCase;
    
    @Override
    public Map<String, Object> getWomanRecommendation(Map<String, Object> womanHistory) throws IOException, InterruptedException {
        try {
            // Extract language and riskModelUri from womanHistory
            String language = (String) womanHistory.getOrDefault("language", "en");
            String riskModelUri = (String) womanHistory.getOrDefault("riskModelUri", DEFAULT_RISK_MODEL_URI);
            
            // Convert Map<String, Object> to Map<String, String>
            Map<String, String> womanHistoryProps = new HashMap<>();
            for (Map.Entry<String, Object> entry : womanHistory.entrySet()) {
                if (entry.getValue() != null) {
                    womanHistoryProps.put(entry.getKey(), entry.getValue().toString());
                }
            }
            
            // Step 1: Create woman and calculate risk
            WomanRisk womanRisk = breastCancerStudiesUseCase.calculateRiskAndCreateWoman(
                riskModelUri, 
                womanHistoryProps, 
                language
            );
            String womanId = womanRisk.getWomanUri();
            String riskLevelUri = womanRisk.getRiskLevelUri();
            
            // Step 2: Get recommendation guide URI based on risk level
            List<BreastCancerStudiesUseCase.IndividualDescriptor> guidelines = 
                breastCancerStudiesUseCase.getAllGuidelinesFor(riskLevelUri, language);
            
            if (guidelines.isEmpty()) {
                throw new IOException("No guidelines found for risk level: " + riskLevelUri);
            }
            
            // Use the first guideline (you may want to add logic to select a specific guideline)
            String guidelineUri = guidelines.get(0).uri();
            
            // Step 3: Get breast cancer recommendation
            WomanRecommendation recommendation = breastCancerStudiesUseCase.getWomanAllRecommendations(
                womanId, 
                guidelineUri, 
                language
            );
            
            // Build response with only womanId and recommendations (both mid and high)
            Map<String, Object> response = new HashMap<>();
            response.put("womanId", womanId);
            response.put("midRecommendation", recommendation != null ? recommendation.getMidRecommendation() : null);
            response.put("highRecommendation", recommendation != null ? recommendation.getHighRecommendation() : null);
            
            return response;
            
        } catch (Exception e) {
            throw new IOException("Error getting woman recommendation: " + e.getMessage(), e);
        }
    }
}
