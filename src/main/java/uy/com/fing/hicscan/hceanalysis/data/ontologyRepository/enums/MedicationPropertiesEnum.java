package uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Enumerado con las propiedades de medicamentos de la ontología breast_cancer_recommendation.
 */
@AllArgsConstructor
@Getter
public enum MedicationPropertiesEnum {
    HAS_ACTIVE_INGREDIENT("http://purl.org/ontology/breast_cancer_recommendation#hasActiveIngredient"),
    HAS_HISTORY("http://purl.org/ontology/breast_cancer_recommendation#hasHistory"),
    HAS_LABEL("http://www.w3.org/2000/01/rdf-schema#label"),
    HAS_PREF_LABEL("http://www.w3.org/2004/02/skos/core#prefLabel"),
    HAS_ALT_LABEL("http://www.w3.org/2004/02/skos/core#altLabel");

    private final String uri;

    public Property prop() {
        return ResourceFactory.createProperty(getUri());
    }
}
