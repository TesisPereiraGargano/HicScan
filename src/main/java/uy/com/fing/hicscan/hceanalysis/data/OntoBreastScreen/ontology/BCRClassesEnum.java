package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Este enumerado tiene todas las clases de la ontología breast_cancer_recommendation junto
 * a sus uris.
 */
@AllArgsConstructor
@Getter
public enum BCRClassesEnum {
    WOMAN_CLASS("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C14284"),
    HISTORY_CLASS("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#C54625"),
    AGE_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Age"),
    HEIGHT_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Height"),
    WEIGHT_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Weight"),
    REC_INTERVAL_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Rec_interval"),
    MODEL_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Model"),
    GUIDELINE_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Guideline"),
    RISK_FACTOR_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Risk_factor"),
    RISK_CLASS("http://purl.org/ontology/breast_cancer_recommendation#Risk");

    private final String uri;
}

