package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.calculators;

import org.springframework.stereotype.Component;

import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskLevel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModelCalculatorType;

import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRQuestionAnswersInstancesEnum.*;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRQuestionInstancesEnum.*;

import java.util.Map;

/**
 * Calculadora de MSP UY siguiendo reglas de la guìa.
 */
@Component
public class MSPUyGuideRiskCalculator extends RiskCalculator {

    @Override
    public RiskCalculation doRiskCalculation(Map<String, String> womanFormData) {
        RiskLevel resultRisk = RiskLevel.MEDIUM;

        if(UY_HEREDITARY_RISK_YES.getUri().equals(womanFormData.get(UY_HEREDITARY_RISK_QUESTION.getUri())) ||
                UY_CHEST_RADIOTERAPHY_YES.getUri().equals(womanFormData.get(UY_CHEST_RADIOTERAPHY_QUESTION.getUri())) ||
                UY_HIPERPLASIA_ATIPIA_YES.getUri().equals(womanFormData.get(UY_HIPERPLASIA_ATIPIA_QUESTION.getUri()))) {
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
        return RiskModelCalculatorType.MSP_UY_GUIDELINE;
    }

    @Override
    public RiskModel getRiskModel() {
        return RiskModel.MSP_UY;
    }

}
