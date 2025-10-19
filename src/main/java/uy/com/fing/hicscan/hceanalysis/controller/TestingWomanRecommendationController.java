package uy.com.fing.hicscan.hceanalysis.controller;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.ReasoningResult;
import uy.com.fing.hicscan.hceanalysis.usecases.InstanciateOntology;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hicscan-api/testing/woman-recommendation")
@Slf4j
public class TestingWomanRecommendationController {

    private final InstanciateOntology instanciateOntology;

    public TestingWomanRecommendationController(InstanciateOntology instanciateOntology) {
        this.instanciateOntology = instanciateOntology;
    }

    /**
     * Request DTO simplificado que recibe solo los datos de la mujer
     */
    public static class CompleteWomanProcessingRequest {
        private Map<String, String> womanHistoryData = new HashMap<>();
        
        @JsonAnySetter
        public void setWomanHistoryData(String key, String value) {
            this.womanHistoryData.put(key, value);
        }
        
        @JsonAnyGetter
        public Map<String, String> getWomanHistoryData() {
            return womanHistoryData;
        }
        
        public CompleteWomanProcessingRequest() {}
    }

    /**
     * Response DTO para el procesamiento completo
     */
    public record CompleteWomanProcessingResponse(
            boolean success,
            String message,
            ReasoningResult reasoningResult) {
    }

    /**
     * Endpoint para procesar una mujer completa con medicamento y razonamiento
     */
    @PostMapping("/complete-processing")
    public ResponseEntity<CompleteWomanProcessingResponse> processWomanWithMedicationAndReasoning(
            @RequestBody CompleteWomanProcessingRequest request) {
        try {
            // Valores por defecto para el procesamiento
            RiskModel riskModel = RiskModel.MSP_UY; // Usar MSP_UY como modelo por defecto
            
            // Valores por defecto para medicamento
            String medicationName = "Hydrochlorothiazide";
            String activeIngredient = "Hydrochlorothiazide";
            String code = "C0020255";
            boolean isDiuretic = true;

            // Ejecutar el procesamiento completo
            ReasoningResult result = instanciateOntology.processWomanWithMedicationAndReasoning(
                    riskModel,
                    request.getWomanHistoryData(),
                    medicationName,
                    activeIngredient,
                    code,
                    isDiuretic);

            return ResponseEntity.ok(new CompleteWomanProcessingResponse(
                    result.isSuccess(),
                    result.isSuccess() ? "Procesamiento completado exitosamente" : "Error en el procesamiento",
                    result));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CompleteWomanProcessingResponse(
                    false,
                    "Error interno del servidor: " + e.getMessage(),
                    new ReasoningResult(List.of(), List.of(), 0, false, e.getMessage())));
        }
    }

}
