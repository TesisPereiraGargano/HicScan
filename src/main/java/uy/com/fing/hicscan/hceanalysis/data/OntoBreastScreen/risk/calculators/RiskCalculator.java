package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.calculators;

import java.util.Map;

import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModelCalculatorType;

public abstract class RiskCalculator {

    public final RiskCalculation calculateRisk(Map<String, String> womanFormData) {
        return doRiskCalculation(womanFormData);
    }

    public abstract RiskCalculation doRiskCalculation(Map<String, String> womanFormData);
    public abstract RiskModelCalculatorType getRiskModelCalculator();
    public abstract RiskModel getRiskModel();

}
