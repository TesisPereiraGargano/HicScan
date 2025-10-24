package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Este enumerado tiene todas las propiedades de la ontología breast_cancer_recommendation junto
 * a sus uris.
 */
@AllArgsConstructor
@Getter
public enum BCRPropsEnum {
    HAS_RISK_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasRisk"),
    HAS_IMAGING_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasImaging"),
    GIVES_PROP("http://purl.org/ontology/breast_cancer_recommendation#gives"),
    HAS_REC_STRENGTH_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasRecStrength"),
    HAS_PERIODICITY_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasPeriodicity"),
    FOR_INTERVAL_PROP("http://purl.org/ontology/breast_cancer_recommendation#forInterval"),
    FOR_RISK_LEVEL_PROP("http://purl.org/ontology/breast_cancer_recommendation#forRiskLevel"),
    HAS_RISK_LEVEL("http://purl.org/ontology/breast_cancer_recommendation#hasLevel"),
    AGE_DPROP("http://purl.org/ontology/breast_cancer_recommendation#age"),
    HAS_RECOMMENDATION_HIGH_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasRecommendationHigh"),
    HAS_RECOMMENDATION_MID_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasRecommendationMedium"),
    INTERVAL_MAX_PROP("http://purl.org/ontology/breast_cancer_recommendation#intervalMax"),
    INTERVAL_MIN_PROP("http://purl.org/ontology/breast_cancer_recommendation#intervalMin"),
    HAS_RANGE_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasRange"),
    HAS_AGE_PROP("http://purl.org/ontology/breast_cancer_recommendation#hasAge"),
    HAS_MODEL_QUESTION("http://purl.org/ontology/breast_cancer_recommendation#hasModelQuestion"),
    PREDICTS("http://purl.org/ontology/breast_cancer_recommendation#predicts"),

    QUESTION_ORDER("http://purl.org/ontology/breast_cancer_recommendation#order"),
    QUESTION_HAS_ANSWER("http://purl.org/ontology/breast_cancer_recommendation#hasAnswer"),
    QUESTION_HAS_TEXT("http://purl.org/ontology/breast_cancer_recommendation#questionText"),
    ABOUT_RISK_FACTOR("http://purl.org/ontology/breast_cancer_recommendation#aboutRiskFactor"),
    ANSWER_HAS_TEXT("http://purl.org/ontology/breast_cancer_recommendation#answerText")
    ;

    private final String uri;

    public Property prop() {
        return ResourceFactory.createProperty(getUri());
    }
}

