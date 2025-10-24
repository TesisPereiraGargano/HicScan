package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk;

import org.springframework.stereotype.Service;

import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.calculators.RiskCalculator;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RiskCalculatorService {

    private final Map<RiskModel, RiskCalculator> riskCalculators;

    public RiskCalculatorService(Set<RiskCalculator> riskCalculatorSet) {
        riskCalculators = riskCalculatorSet.stream().collect(Collectors.toMap(
                RiskCalculator::getRiskModel, s -> s));
    }

    public RiskCalculation calculateRiskForModelAndData(String riskModelUri, Map<String, String> womanHistoryProps) {
        return riskCalculators.get(RiskModel.fromString(riskModelUri)).calculateRisk(womanHistoryProps);
    }
}
