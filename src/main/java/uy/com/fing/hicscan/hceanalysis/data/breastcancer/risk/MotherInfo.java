package uy.com.fing.hicscan.hceanalysis.data.breastcancer.risk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotherInfo {

    private Integer currentAgeOrAgeAtDeath;
    private Boolean breastCancer;
    private Integer breastCancerAgeOnSet;
    private Boolean bilateral;
    private Integer motherAgeAtDiagnosisOf2ndBreast;
    private Boolean ovarianCancer;
    private Integer ovarianCancerAgeOnSet;
    private String brcaGene;

}

