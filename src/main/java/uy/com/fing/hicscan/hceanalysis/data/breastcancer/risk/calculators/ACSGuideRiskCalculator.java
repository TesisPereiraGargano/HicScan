package uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.calculators;

import org.springframework.stereotype.Component;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskLevel;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskModelCalculatorType;

import java.util.Map;

import static uy.com.fing.hicscan.hceanalysis.data.breastcancer.ontology.BCRQuestionAnswersInstancesEnum.*;
import static uy.com.fing.hicscan.hceanalysis.data.breastcancer.ontology.BCRQuestionInstancesEnum.*;

/**
 * Calculadora de ACS siguiendo reglas de la guìa.
 */
@Component
public class ACSGuideRiskCalculator extends RiskCalculator {

    @Override
    public RiskCalculation doRiskCalculation(Map<String, String> womanFormData) {
        RiskLevel resultRisk = RiskLevel.MEDIUM;

        if(ACS_CHEST_RADIOTERAPY_YOUNG_AGE_YES.getUri().equals(womanFormData.get(ACS_CHEST_RADIOTERAPY_YOUNG_AGE_QUESTION.getUri())) ||
                ACS_HISTORY_BREAST_CANCER_YES.getUri().equals(womanFormData.get(ACS_HISTORY_BREAST_CANCER_QUESTION.getUri())) ||
                ACS_GENETIC_MUTATION_YES.getUri().equals(womanFormData.get(ACS_GENETIC_MUTATION_QUESTION.getUri()))) {
            resultRisk = RiskLevel.HIGH;
        }

        return RiskCalculation.builder()
                .riskLevel(resultRisk)
                .riskModelCalculatorType(getRiskModelCalculator())
                .riskModel(getRiskModel())
                .build();
    }

    @Override
    public RiskModelCalculatorType getRiskModelCalculator() {
        return RiskModelCalculatorType.ACS_GUIDELINE;
    }

    @Override
    public RiskModel getRiskModel() {
        return RiskModel.ACS;
    }

}
