package uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RiskCalculation {

    private Double riskTenYears;
    private Double riskAllLife;
    private RiskLevel riskLevel;
    private RiskModel riskModel;
    private RiskModelCalculatorType riskModelCalculatorType;
}

