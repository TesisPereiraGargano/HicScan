package uy.com.fing.hicscan.hceanalysis.usecases;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.OntModel;
import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.OntologyRepository;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontoforms.OntoFormsClient;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.OntologyOperations;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.ReasoningResult;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.WomanRisk;

import java.util.ArrayList;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class InstanciateOntology {

    private final OntologyRepository ontologyRepository;
    private final OntoFormsClient ontoFormsClient;
    private final OntologyOperations ontologyOperations;
    private final BreastCancerStudiesUseCase breastCancerStudiesUseCase;

    /**
     * Función principal que orquesta todo el proceso:
     * 1. Crea una instancia de la ontología
     * 2. Crea un nuevo individuo mujer
     * 3. Crea medicamentos para la mujer
     * 4. Ejecuta el razonador
     * 5. Devuelve los resultados del razonamiento
     * 
     * @param riskModel        modelo de riesgo
     * @param womanHistoryData datos de la mujer
     * @param medicationName   nombre del medicamento
     * @param activeIngredient ingrediente activo del medicamento
     * @param code             código del medicamento
     * @param isDiuretic       si el medicamento es diurético
     * @return ReasoningResult con los resultados del razonamiento
     */
    public ReasoningResult processWomanWithMedicationAndReasoning(RiskModel riskModel,
            Map<String, String> womanHistoryData,
            String medicationName, String activeIngredient,
            String code, boolean isDiuretic) {
        log.info("Starting complete woman processing with medication and reasoning");

        try {
            // 1. Crear una instancia de la ontología CON razonamiento configurado pero no ejecutado automáticamente
            OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontoFormsClient.getOntologyFileName());

            if (ontoModel == null) {
                throw new IllegalStateException("No existe la ontología de Breast Cancer Recommendation");
            }

            log.info("Created fresh ontology model instance");

            // 2. Calcular riesgo y crear mujer
            // Necesitamos obtener el URI del modelo de riesgo para
            // calculateRiskAndCreateWoman
            String riskModelUri = "http://purl.org/ontology/breast_cancer_recommendation#UY_model";
            String language = "en"; // Idioma por defecto

            WomanRisk womanRisk = breastCancerStudiesUseCase.calculateRiskAndCreateWoman(ontoModel, riskModelUri, womanHistoryData,
                    language);
            String womanId = womanRisk.getWomanUri();
            log.info("Created woman individual with ID: {} and risk level: {}", womanId, womanRisk.getRiskLevelUri());

            // 3. Crear medicamento para la mujer
            boolean medicationCreated = ontologyOperations.createMedicationForWoman(
                    ontoModel, womanId, medicationName, activeIngredient, code, isDiuretic);

            if (!medicationCreated) {
                log.warn("Failed to create medication for woman {}", womanId);
            } else {
                log.info("Successfully created medication {} for woman {}", medicationName, womanId);
            }

            // 4. Ejecutar el razonador
            ReasoningResult reasoningResult = ontologyOperations.executeReasoner(ontoModel);
            log.info("Reasoning completed. Success: {}, Total statements: {}",
                    reasoningResult.isSuccess(), reasoningResult.getTotalStatements());

            // 5. Limpiar recursos del modelo
            ontoModel.close();

            return reasoningResult;

        } catch (Exception e) {
            log.error("Error in complete woman processing: {}", e.getMessage(), e);
            return new ReasoningResult(new ArrayList<>(), new ArrayList<>(), 0, false, e.getMessage());
        }
    }
}
