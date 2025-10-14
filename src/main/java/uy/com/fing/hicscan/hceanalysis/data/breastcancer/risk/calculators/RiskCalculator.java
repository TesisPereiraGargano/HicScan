package uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.calculators;

import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos.RiskModelCalculatorType;

import java.util.Map;

public abstract class RiskCalculator {

    public final RiskCalculation calculateRisk(Map<String, String> womanFormData) {
        return doRiskCalculation(womanFormData);
    }

    public abstract RiskCalculation doRiskCalculation(Map<String, String> womanFormData);
    public abstract RiskModelCalculatorType getRiskModelCalculator();
    public abstract RiskModel getRiskModel();

}
