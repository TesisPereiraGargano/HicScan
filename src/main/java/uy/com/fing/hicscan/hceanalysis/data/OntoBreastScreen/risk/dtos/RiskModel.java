package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Representa a todos los modelos de riesgo del sistema.
 */
@AllArgsConstructor
@Getter
public enum RiskModel {
    IBIS("http://purl.org/ontology/breast_cancer_recommendation#IBIS_model"),
    GAIL("http://purl.org/ontology/breast_cancer_recommendation#Gail_model"),
    ACS("http://purl.org/ontology/breast_cancer_recommendation#ACS_model"),
    MSP_UY("http://purl.org/ontology/breast_cancer_recommendation#UY_model");

    private final String individualUri;

    public static RiskModel fromString(String riskModelUri) {
        return Arrays.stream(RiskModel.values()).filter(
                m -> m.getIndividualUri().equals(riskModelUri)
        ).findFirst().orElse(IBIS);
    }
}

