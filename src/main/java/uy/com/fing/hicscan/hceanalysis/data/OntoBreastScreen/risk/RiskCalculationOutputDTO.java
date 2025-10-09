package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk;

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

