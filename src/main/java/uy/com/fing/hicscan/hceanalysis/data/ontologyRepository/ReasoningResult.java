package uy.com.fing.hicscan.hceanalysis.data.ontologyRepository;

import java.util.List;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.recommend.WomanRecommendation;

/**
 * Clase para encapsular los resultados del razonador
 */
public class ReasoningResult {
    private final List<String> derivedStatements;
    private final List<String> derivations;
    private final int totalStatements;
    private final boolean success;
    private final String errorMessage;
    private final WomanRecommendation womanRecommendation;

    public ReasoningResult(List<String> derivedStatements, List<String> derivations, 
                          int totalStatements, boolean success, String errorMessage,
                          WomanRecommendation womanRecommendation) {
        this.derivedStatements = derivedStatements;
        this.derivations = derivations;
        this.totalStatements = totalStatements;
        this.success = success;
        this.errorMessage = errorMessage;
        this.womanRecommendation = womanRecommendation;
    }

    public List<String> getDerivedStatements() { 
        return derivedStatements; 
    }
    
    public List<String> getDerivations() { 
        return derivations; 
    }
    
    public int getTotalStatements() { 
        return totalStatements; 
    }
    
    public boolean isSuccess() { 
        return success; 
    }
    
    public String getErrorMessage() { 
        return errorMessage; 
    }
    
    public WomanRecommendation getWomanRecommendation() {
        return womanRecommendation;
    }
}
