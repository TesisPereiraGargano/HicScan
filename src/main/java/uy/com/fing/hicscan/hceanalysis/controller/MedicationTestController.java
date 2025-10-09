package uy.com.fing.hicscan.hceanalysis.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.persistence.WomanIndividualsRepository;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.OntologyRepository;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRClassesEnum;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRPropsEnum;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.FusekiTripleStoreClient;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontoforms.OntoFormsClient;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/test/medication")
public class MedicationTestController {

    private final WomanIndividualsRepository womanIndividualsRepository;
    private final OntologyRepository ontologyRepository;
    private final FusekiTripleStoreClient fusekiTripleStoreClient;
    private final OntoFormsClient ontoFormsClient;

    /**
     * Request DTO para crear un medicamento para una mujer.
     */
    public record CreateMedicationRequest(
        String ontologyId,
        String womanId,
        String medicationName,
        String activeIngredient,
        String code,
        boolean isDiuretic
    ) {}

    /**
     * Response DTO para la operación de medicamento.
     */
    public record MedicationResponse(
        boolean success,
        String message,
        Object data
    ) {}

    /**
     * Request DTO para crear una mujer de prueba.
     */
    public record CreateTestWomanRequest(
        String ontologyId
    ) {}

    /**
     * Response DTO para la creación de mujer de prueba.
     */
    public record TestWomanResponse(
        boolean success,
        String message,
        String womanId,
        String womanUri
    ) {}

    /**
     * Response DTO para listar ontologías.
     */
    public record OntologiesResponse(
        boolean success,
        String message,
        List<String> ontologies
    ) {}

    /**
     * Lista las ontologías disponibles en Fuseki usando nuestra implementación.
     * 
     * @return respuesta con la lista de ontologías
     */
    @GetMapping("/list-ontologies")
    public ResponseEntity<OntologiesResponse> listOntologies() {
        log.info("Consulting available ontologies in Fuseki using FusekiTripleStoreClient");
        
        try {
            // Usar nuestro cliente de Fuseki para obtener las ontologías
            List<String> ontologies = fusekiTripleStoreClient.getAvailableOntologies();
            
            log.info("Found {} ontologies: {}", ontologies.size(), ontologies);
            
            return ResponseEntity.ok(new OntologiesResponse(
                true,
                "Ontologies retrieved successfully using FusekiTripleStoreClient",
                ontologies
            ));
            
        } catch (Exception e) {
            log.error("Error consulting ontologies: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new OntologiesResponse(
                false,
                "Error consulting ontologies: " + e.getMessage(),
                List.of()
            ));
        }
    }


    /**
     * Crea una mujer de prueba para testing.
     * 
     * @param request la solicitud con el ID de la ontología
     * @return respuesta con el ID de la mujer creada
     */
    @PostMapping("/create-test-woman")
    public ResponseEntity<TestWomanResponse> createTestWoman(@RequestBody CreateTestWomanRequest request) {
        log.info("Creating test woman using ontoFormsClient.getOntologyFileName()");
        
        try {
            // Usar el método correcto para obtener el ID de la ontología
            String ontologyId = ontoFormsClient.getOntologyFileName();
            log.info("Using ontology ID: {}", ontologyId);
            
            // Obtener el modelo de la ontología
            OntModel ontoModel = ontologyRepository.getOntologyModelABoxByIdFor(ontologyId);
            
            if (ontoModel == null) {
                return ResponseEntity.badRequest().body(new TestWomanResponse(
                    false,
                    "Ontology not found: " + ontologyId,
                    null,
                    null
                ));
            }
            
            // Crear una mujer individual
            Individual womanIndividual = ontoModel.getOntClass(BCRClassesEnum.WOMAN_CLASS.getUri()).createIndividual();
            
            // Agregar algunas propiedades básicas
            womanIndividual.addProperty(BCRPropsEnum.HAS_AGE_PROP.prop(), "35");
            womanIndividual.addProperty(BCRPropsEnum.AGE_DPROP.prop(), "35");
            
            // Guardar en el repositorio en memoria
            String womanId = womanIndividualsRepository.saveWomanIndividual(womanIndividual);
            
            log.info("Created test woman with ID: {} and URI: {}", womanId, womanIndividual.getURI());
            
            return ResponseEntity.ok(new TestWomanResponse(
                true,
                "Test woman created successfully",
                womanId,
                womanIndividual.getURI()
            ));
            
        } catch (Exception e) {
            log.error("Error creating test woman: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(new TestWomanResponse(
                false,
                "Error creating test woman: " + e.getMessage(),
                null,
                null
            ));
        }
    }
}
