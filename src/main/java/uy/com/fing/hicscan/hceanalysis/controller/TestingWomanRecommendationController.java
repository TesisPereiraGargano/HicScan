package uy.com.fing.hicscan.hceanalysis.controller;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.ReasoningResult;
import uy.com.fing.hicscan.hceanalysis.dto.*;
import uy.com.fing.hicscan.hceanalysis.usecases.InstanciateOntology;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.risk.dtos.RiskModel;

import java.util.ArrayList;
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

            DatosHCE datosHCE = new DatosHCE();

            // Datos básicos del paciente
            PacienteExtendido paciente = new PacienteExtendido("Juan Perez","M","19900924",
                    null, null, null, "1.77", "m", "88.0", "kg");

            datosHCE.setDatosBasicosPaciente(paciente);

            // Inicialización de medicamentos
            DatosHCE.Medicamentos medicamentos = new DatosHCE.Medicamentos();
            DatosHCE.Medicamentos.Clasificados clasificados = new DatosHCE.Medicamentos.Clasificados();

            // 1. Diurético
            CodDiccionario codigosDiuretico = new CodDiccionario();
            codigosDiuretico.setSnomedCT("376209006");
            codigosDiuretico.setRxnorm("310798");
            codigosDiuretico.setCui("C0977518");
            Droga drogaDiuretico = new Droga(codigosDiuretico, "Hydrochlorothiazide 25mg tablet");

            SustanciaAdministrada diuretico = new SustanciaAdministrada("HCTZ 25mg una vez al día", "mg", "25",
                    "24", "h",List.of(drogaDiuretico));

            clasificados.setDiureticos(List.of(diuretico));

            // 2. No diurético
            CodDiccionario codigosNoDiuretico = new CodDiccionario();
            codigosNoDiuretico.setSnomedCT("66493003");
            codigosNoDiuretico.setRxnorm("10438");
            codigosNoDiuretico.setCui("C0039771");
            Droga drogaNoDiuretico = new Droga(codigosNoDiuretico, "Theophylline");

            SustanciaAdministrada noDiuretico = new SustanciaAdministrada("Theodur 200 mg dos veces al día","mg","200",
                    "h", "12", List.of(drogaNoDiuretico));

            clasificados.setNoDiureticos(List.of(noDiuretico));

            // 3. No clasificado
            CodDiccionario codigosNoClasificado = new CodDiccionario();
            codigosNoClasificado.setCui("C0038317");
            Droga drogaNoClasificado = new Droga(codigosNoClasificado, "");

            SustanciaAdministrada noClasificado = new SustanciaAdministrada("Steroids", "", "", "", "",
                    List.of(drogaNoClasificado));

            medicamentos.setClasificados(clasificados);
            medicamentos.setNoClasificados(List.of(noClasificado));

            datosHCE.setMedicamentos(medicamentos);

            // Ejecutar el procesamiento completo
            ReasoningResult result = instanciateOntology.processWomanWithMedicationAndReasoning(
                    riskModel,
                    request.getWomanHistoryData(),
                    datosHCE);

            return ResponseEntity.ok(new CompleteWomanProcessingResponse(
                    result.isSuccess(),
                    result.isSuccess() ? "Procesamiento completado exitosamente" : "Error en el procesamiento",
                    result));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CompleteWomanProcessingResponse(
                    false,
                    "Error interno del servidor: " + e.getMessage(),
                    new ReasoningResult(List.of(), List.of(), 0, false, e.getMessage(), null)));
        }
    }

}
