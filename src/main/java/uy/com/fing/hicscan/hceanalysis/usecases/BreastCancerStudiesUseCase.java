/*
 * FILE FROM ONTOBREASTSCREEN PROJECT
 * IT HAS SOME MODIFICATIONS TO BE ABLE TO ADD MEDICATIONS TO THE WOMAN INDIVIDUAL IN THE SAME ONTOLOGY MODEL
 */

package uy.com.fing.hicscan.hceanalysis.usecases;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontoforms.OntoFormsClient;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRPropsEnum;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.OntologyRepository;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.persistence.WomanIndividualsRepository;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.recommend.WomanRecommendation;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.RiskCalculationOutputDTO;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.RiskCalculatorService;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.WomanRisk;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskCalculation;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskLevel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.springframework.stereotype.Service;

import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRClassesEnum.GUIDELINE_CLASS;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRClassesEnum.WOMAN_CLASS;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRPropsEnum.*;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRQuestionInstancesEnum.*;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.utils.IndividualMappingUtils.getAgeIndividual;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.utils.IndividualMappingUtils.getRiskIndividual;
import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.utils.LanguageLabelUtils.getLabelInLanguageOrDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class BreastCancerStudiesUseCase {

    private static final int DEFAULT_WOMAN_AGE = 40;
    private final OntologyRepository ontologyRepository;
    private final WomanIndividualsRepository womanIndividualsRepository;
    private final RiskCalculatorService riskCalculatorService;
    private final OntoFormsClient ontoFormsClient;

    public record IndividualDescriptor(String uri, String prettyName) {
    }

    public List<IndividualDescriptor> getModels(String language) {
        OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontoFormsClient.getOntologyFileName());

        return ontoModel.listIndividuals(ResourceFactory.createResource(
                uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRClassesEnum.MODEL_CLASS.getUri()))
                .mapWith(in -> new IndividualDescriptor(in.getURI(),
                        getLabelInLanguageOrDefault(in, language)))
                .toList();
    }

    /**
     * Calcula el riesgo y genera una nueva instancia de mujer.
     *
     * @param riskModelUri    modelo de riesgo
     * @param womanHistoryProps datos de la mujer.
     * @return representación riesgo mujer e identificador individuo.
     */
    public WomanRisk calculateRiskAndCreateWoman(String riskModelUri, Map<String, String> womanHistoryProps,
            String language) {
        OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontoFormsClient.getOntologyFileName());
        return calculateRiskAndCreateWoman(ontoModel, riskModelUri, womanHistoryProps, language);
    }

    public WomanRisk calculateRiskAndCreateWoman(OntModel ontoModel, String riskModelUri,
            Map<String, String> womanHistoryProps, String language) {

        RiskCalculation calculatedRisk = riskCalculatorService.calculateRiskForModelAndData(riskModelUri,
                womanHistoryProps);

        Individual womanRisk = ontoModel.getIndividual(calculatedRisk.getRiskLevel().getUri());

        String womanUri = createNewWomanIndividual(ontoModel, calculatedRisk.getRiskModel(),
                calculatedRisk.getRiskLevel(), womanHistoryProps);

        return WomanRisk.builder()
                .womanUri(womanUri)
                .riskCalculation(RiskCalculationOutputDTO.builder()
                        .riskModelCalculator(calculatedRisk.getRiskModelCalculatorType().getDescription())
                        .riskTenYears(calculatedRisk.getRiskTenYears())
                        .riskAllLife(calculatedRisk.getRiskAllLife())
                        .riskModel(calculatedRisk.getRiskModel().name())
                        .build())
                .riskLevel(getLabelInLanguageOrDefault(womanRisk, language))
                .riskLevelUri(womanRisk.getURI())
                .build();
    }

    /**
     * Genera un nuevo individuo de la clase Mujer y le agrega la relación de
     * hasRisk, hasAge y recomendaciones. - MODIFICADA PARA RECIBIR ONTOMODEL Y NO
     * CREAR UNO NUEVO
     * 
     * @param ontoModel        modelo de la ontología
     * @param riskModel        modelo de riesgo
     * @param riskLevel        riesgo calculado para el individuo.
     * @param womanHistoryData datos de la mujer.
     * @return individuo mujer construido.
     */
    public String createNewWomanIndividual(OntModel ontoModel, RiskModel riskModel, RiskLevel riskLevel,
            Map<String, String> womanHistoryData) {
        if (ontoModel == null) {
            throw new IllegalStateException("No existe la ontología de Breast Cancer Recommendation");
        }

        Individual patient = ontoModel.getOntClass(WOMAN_CLASS.getUri())
                .createIndividual("http://purl.org/ontology/breast_cancer_recommendation#NewWoman");

        try {
            addRiskRelationship(patient, riskModel, riskLevel);
        } catch (NullPointerException e) {
            System.out.println(
                    "NullPointerException: Puede que WOMAN_CLASS.getUri() o getOntClass() haya devuelto null.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Ha ocurrido una excepción inesperada:");
            e.printStackTrace();
        }

        String ageString = null;
        if (riskModel == RiskModel.MSP_UY) {
            ageString = womanHistoryData.get(UY_AGE_QUESTION.getUri());
        } else if (riskModel == RiskModel.ACS) {
            ageString = womanHistoryData.get(ACS_AGE_QUESTION.getUri());
        } else if (riskModel == RiskModel.IBIS) {
            ageString = womanHistoryData.get(IBIS_PERSONAL_AGE_QUESTION.getUri());
        }

        int declaredAge = ageString != null ? Integer.parseInt(ageString) : DEFAULT_WOMAN_AGE;
        addHasAgeRelationship(patient, declaredAge);

        return womanIndividualsRepository.saveWomanIndividual(patient);
    }

    /**
     * @param womanId
     * @param guideUri
     * @return
     */
    public List<String> getExplainFor(String womanId, String guideUri) {
        var patient = womanIndividualsRepository.getWoman(womanId);
        var ontoModel = patient.getOntModel();

        ontoModel.setDerivationLogging(true);

        var guideline = ontoModel.getIndividual(guideUri);

        String uri = guideline.getPropertyValue(FOR_RISK_LEVEL_PROP.prop()).as(Individual.class).getURI();

        Individual recommendationIndividual;

        if (RiskLevel.MEDIUM.getUri().equals(uri)) {
            recommendationIndividual = patient.listPropertyValues(HAS_RECOMMENDATION_MID_PROP.prop())
                    .mapWith(node -> node.as(Individual.class))
                    .filterKeep(recommendation -> guideline.listPropertyValues(GIVES_PROP.prop())
                            .mapWith(recomm -> recomm.as(Individual.class).getURI())
                            .filterKeep(recomm -> recomm.equals(recommendation.getURI())).hasNext())
                    .nextOptional()
                    .orElse(null);
        } else {
            recommendationIndividual = patient.listPropertyValues(HAS_RECOMMENDATION_HIGH_PROP.prop())
                    .mapWith(node -> node.as(Individual.class))
                    .filterKeep(recommendation -> guideline.listPropertyValues(GIVES_PROP.prop())
                            .mapWith(recomm -> recomm.as(Individual.class).getURI())
                            .filterKeep(recomm -> recomm.equals(recommendation.getURI())).hasNext())
                    .nextOptional()
                    .orElse(null);
        }

        List<String> explainList = new ArrayList<>();
        if (recommendationIndividual != null) {

            List<Statement> list = ontoModel
                    .listStatements(patient, HAS_RECOMMENDATION_HIGH_PROP.prop(), recommendationIndividual).toList();

            Statement principalStmt = list.get(0);
            ontoModel.getDerivation(principalStmt).forEachRemaining(d -> explainList.add(d.toString()));
        }

        return explainList;
    }

    /**
     * Dado un individuo de la clase Mujer, obtiene todas las recomendaciones de
     * estudios relacionadas mediante
     * las objectProperties {@link BCRPropsEnum#HAS_RECOMMENDATION_HIGH_PROP} y
     * {@link BCRPropsEnum#HAS_RECOMMENDATION_MID_PROP}
     * 
     * @return Objeto WomanRecomendation que contiene las recomendaciones MID y
     *         HIGH.
     */
    public WomanRecommendation getWomanAllRecommendations(OntModel ontoModel, String womanId, String guideUri,
            String language) {
        var patient = womanIndividualsRepository.getWoman(womanId);
        // var ontoModel = patient.getOntModel();
        var guideline = ontoModel.getIndividual(guideUri);

        String uri = guideline.getPropertyValue(FOR_RISK_LEVEL_PROP.prop()).as(Individual.class).getURI();

        if (RiskLevel.MEDIUM.getUri().equals(uri)) {
            // Guìa para riesgo medio.
            return getRecommendationFilteredWithGuide(ontoModel, patient, guideline, HAS_RECOMMENDATION_MID_PROP.prop(),
                    recomm -> WomanRecommendation.builder()
                            .midRecommendation(buildRecommendationDTOFromIndividual(recomm, language)).build());
        } else {
            // Guìa para riesgo alto.
            return getRecommendationFilteredWithGuide(ontoModel, patient, guideline,
                    HAS_RECOMMENDATION_HIGH_PROP.prop(), recomm -> WomanRecommendation.builder()
                            .highRecommendation(buildRecommendationDTOFromIndividual(recomm, language)).build());
        }
    }

    public List<IndividualDescriptor> getAllGuidelinesFor(String riskLevelUri, String language) {
        OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontoFormsClient.getOntologyFileName());

        if (ontoModel == null) {
            throw new IllegalStateException("No existe la ontología de Breast Cancer Recommendation");
        }

        return ontoModel.listIndividuals(ResourceFactory.createResource(GUIDELINE_CLASS.getUri()))
                .filterKeep(guide -> riskLevelUri
                        .equals(guide.getPropertyValue(FOR_RISK_LEVEL_PROP.prop()).asNode().getURI()))
                .mapWith(
                        guide -> new IndividualDescriptor(guide.getURI(), getLabelInLanguageOrDefault(guide, language)))
                .toList();
    }

    private void addHasAgeRelationship(Individual patient, Integer declaredAge) {
        var ontoModel = patient.getOntModel();
        Individual ageIndividual = getAgeIndividual(ontoModel, declaredAge);
        patient.addProperty(HAS_AGE_PROP.prop(), ageIndividual);
    }

    private void addRiskRelationship(Individual patient, RiskModel riskModel, RiskLevel riskLevel) {
        var ontoModel = patient.getOntModel();
        Individual riskIndividual = getRiskIndividual(ontoModel, riskModel, riskLevel);
        patient.addProperty(HAS_RISK_PROP.prop(), riskIndividual);
    }

    private WomanRecommendation.Recommendation buildRecommendationDTOFromIndividual(Individual recommendation,
            String language) {
        var imagingRec = getLabelInLanguageOrDefault(recommendation.getPropertyValue(HAS_IMAGING_PROP.prop())
                .as(Individual.class), language);

        var strengthPropRec = getLabelInLanguageOrDefault(recommendation.getPropertyValue(HAS_REC_STRENGTH_PROP.prop())
                .as(Individual.class), language);

        var periodicityRec = getLabelInLanguageOrDefault(recommendation.getPropertyValue(HAS_PERIODICITY_PROP.prop())
                .as(Individual.class), language);

        var forIntervalRec = getLabelInLanguageOrDefault(recommendation.getPropertyValue(FOR_INTERVAL_PROP.prop())
                .as(Individual.class), language);

        return new WomanRecommendation.Recommendation(imagingRec, strengthPropRec, periodicityRec, forIntervalRec);
    }


    private WomanRecommendation getRecommendationFilteredWithGuide(OntModel ontoModel, Individual patient,
    Individual guideline, Property recommendationProp,
    Function<Individual, WomanRecommendation> builderF) {

        // Paso 1: Obtener todas las recomendaciones del paciente
        NodeIterator patientRecommendations = patient.listPropertyValues(recommendationProp);

        // Paso 2: Convertir a individuos
        ExtendedIterator<Individual> recommendationIndividuals = patientRecommendations
                .mapWith(node -> node.as(Individual.class));

        // Paso 3: Filtrar las recomendaciones que están en la guía
        ExtendedIterator<Individual> filteredRecommendations = recommendationIndividuals
                .filterKeep(recommendation -> guideline.listPropertyValues(GIVES_PROP.prop())
                        .mapWith(recomm -> recomm.as(Individual.class).getURI())
                        .filterKeep(recomm -> recomm.equals(recommendation.getURI())).hasNext());

        // Paso 4: Tomar la primera recomendación que cumple el filtro
        Optional<Individual> firstRecommendation = filteredRecommendations.nextOptional();

        // Paso 5: Aplicar la función builder o devolver null
        WomanRecommendation recommendations = firstRecommendation
                .map(builderF)
                .orElse(null);

        return recommendations;
    }

}
