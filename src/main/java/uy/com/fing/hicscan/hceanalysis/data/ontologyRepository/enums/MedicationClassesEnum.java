package uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerado con las clases de medicamentos de la ontología breast_cancer_recommendation.
 */
@AllArgsConstructor
@Getter
public enum MedicationClassesEnum {
    MEDICATION_HISTORY_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Medication_History"),
    ACTIVE_INGREDIENT_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Active_ingredient"),
    DIURETIC_CLASS("http://purl.bioontology.org/ontology/ATC/C03");
    private final String uri;
}
