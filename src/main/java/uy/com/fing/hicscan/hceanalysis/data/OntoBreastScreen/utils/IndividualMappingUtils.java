package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskLevel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;

import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRClassesEnum.AGE_CLASS;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRClassesEnum.REC_INTERVAL_CLASS;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRPropsEnum.*;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndividualMappingUtils {

    /**
     * A partir de un riskModel (IBIS, GAIL, ACS) y un riesgo (MID, HIGH) obtiene al individuo de la clase Risk correspondiente.
     * @param model ontoModel
     * @param riskModel modelo de riesgo
     * @param riskLevel nivel de riesgo en ese modelo.
     * @return individuo de la clase Risk.
     */
    public static Individual getRiskIndividual(OntModel model, RiskModel riskModel, RiskLevel riskLevel) {
        return model.getIndividual(riskModel.getIndividualUri())
                .listPropertyValues(PREDICTS.prop())
                .mapWith(node -> node.as(Individual.class))
                .filterKeep(individual -> individual.getPropertyValue(HAS_RISK_LEVEL.prop()).asNode().getURI().equals(riskLevel.getUri()))
                .nextOptional()
                .orElse(null);
    }

    /**
     * Genera un nuevo individuo de la clase age, agregando relación de la edad declarada
     * y generando una relación hasRange para cada intervalo donde la edad pertenezca.
     * @param model ontomodel
     * @param declaredAge edad declarada
     * @return individuo de la clase AGE.
     */
    public static Individual getAgeIndividual(OntModel model, Integer declaredAge) {

        String ageUri = "http://purl.org/ontology/breast_cancer_recommendation#age_" + declaredAge;
        final var ageIndividual = model.getOntClass(AGE_CLASS.getUri()).createIndividual(ageUri);

        //Agrego la declaredAge en prop "age" al nuevo individuo de la clase AGE
        ageIndividual.addLiteral(AGE_DPROP.prop(), declaredAge);

        //Agregamos las relaciones de rec_interval según los mínimos y máximos de cada rec_interval existente.
        var recIntervalClass = model.getOntClass(REC_INTERVAL_CLASS.getUri());
        var intervalMaxProp = model.getProperty(INTERVAL_MAX_PROP.getUri());
        var intervalMinProp = model.getProperty(INTERVAL_MIN_PROP.getUri());
        var hasRangeProp = model.getProperty(HAS_RANGE_PROP.getUri());

        model.listIndividuals(recIntervalClass)
                .filterKeep(recIndividual ->
                        recIndividual.getPropertyValue(intervalMinProp).asLiteral().getInt() <= declaredAge &&
                                recIndividual.getPropertyValue(intervalMaxProp).asLiteral().getInt() >= declaredAge)
                .forEach( recIndividual -> ageIndividual.addProperty(hasRangeProp, recIndividual));

        return ageIndividual;
    }
}

