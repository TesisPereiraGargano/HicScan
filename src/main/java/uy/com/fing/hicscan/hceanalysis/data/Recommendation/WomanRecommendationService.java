package uy.com.fing.hicscan.hceanalysis.data.Recommendation;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for getting woman recommendations based on their history
 */
public interface WomanRecommendationService {
    
    /**
     * Gets a recommendation for a woman based on her history
     * 
     * @param womanHistory Map containing the woman's history data
     * @return Map containing the recommendation response as JSON object
     * @throws IOException if there's an error during the recommendation process
     * @throws InterruptedException if the process is interrupted
     */
    Map<String, Object> getWomanRecommendation(Map<String, Object> womanHistory) throws IOException, InterruptedException;
}
