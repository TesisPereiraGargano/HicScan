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
import java.util.List;

import uy.com.fing.hicscan.hceanalysis.dto.DatosHCE;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;

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
     * @return ReasoningResult con los resultados del razonamiento
     */
    public ReasoningResult processWomanWithMedicationAndReasoning(RiskModel riskModel,
            Map<String, String> womanHistoryData,
            DatosHCE datosHCE) {
        log.info("Starting complete woman processing with medication and reasoning");

        try {
            // 1. Crear una instancia de la ontología CON razonamiento configurado pero no
            // ejecutado automáticamente
            OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontoFormsClient.getOntologyFileName());

            if (ontoModel == null) {
                throw new IllegalStateException("No existe la ontología de Breast Cancer Recommendation");
            }

            // 2. Calcular riesgo y crear mujer
            // Necesitamos obtener el URI del modelo de riesgo para
            // calculateRiskAndCreateWoman
            String riskModelUri = "http://purl.org/ontology/breast_cancer_recommendation#UY_model";
            String language = "en"; // Idioma por defecto

            WomanRisk womanRisk = breastCancerStudiesUseCase.calculateRiskAndCreateWoman(ontoModel, riskModelUri,
                    womanHistoryData,
                    language);

            List<BreastCancerStudiesUseCase.IndividualDescriptor> guidelines = breastCancerStudiesUseCase
                    .getAllGuidelinesFor(womanRisk.getRiskLevelUri(), language);

            String guidelineUri = guidelines.get(0).uri();

            String womanId = womanRisk.getWomanUri();
            
            // WomanRecommendation womanRecommendation = breastCancerStudiesUseCase.getWomanAllRecommendations(womanId, guidelineUri, language);
            breastCancerStudiesUseCase.getWomanAllRecommendations(ontoModel, womanId, guidelineUri, language);

            // 3. Crear medicamentos para la mujer
            boolean medicationsCreated = ontologyOperations.createMedicationsFromHCE(
                    ontoModel, womanId, datosHCE);

            if (!medicationsCreated) {
                log.warn("Failed to create medication for woman {}", womanId);
            } else {
                log.info("Successfully created medication for woman {}",  womanId);
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
