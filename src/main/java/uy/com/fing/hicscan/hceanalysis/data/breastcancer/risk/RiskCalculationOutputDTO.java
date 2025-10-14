package uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RiskCalculationOutputDTO {

    private Double riskTenYears;
    private Double riskAllLife;
    private String riskModel;
    private String riskModelCalculator;
}

