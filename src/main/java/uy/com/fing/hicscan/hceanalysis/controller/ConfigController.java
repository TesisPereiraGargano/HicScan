package uy.com.fing.hicscan.hceanalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import uy.com.fing.hicscan.hceanalysis.usecases.ConfigurationUseCase;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.OntoTree;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.PropertyDescriptorWithFormStatus;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.CalculatedPropertyConfigDescriptor;
import uy.com.fing.hicscan.hceanalysis.data.OntoForms.datatypes.ArtificeClassConfigDescriptor;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("hicscan-api/config/v1") 
public class ConfigController {

    private final ConfigurationUseCase configurationUseCase;

    @Autowired
    public ConfigController(ConfigurationUseCase configurationUseCase) {
        this.configurationUseCase = configurationUseCase;
    }

    public record OntologyCreateResponse(String id, String ontologyName) {};
    public record AppMappingResponse(String mapping) {};

    @GetMapping("/ontologies")
    @ResponseStatus(HttpStatus.OK)
    public String[] getOntologies() {
        return configurationUseCase.getOntologies();
    }

    @GetMapping("/ontologies/{ontoId}/classes")
    @ResponseStatus(HttpStatus.OK)
    public OntoTree getOntologyClasses(@PathVariable String ontoId) {
        if (ontoId == null || ontoId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la ontología es requerido");
        }
        
        return configurationUseCase.getOntologyClasses(ontoId);
    }

    @GetMapping("/ontologies/{ontoId}/classes/form")
    public List<PropertyDescriptorWithFormStatus> getFormToModify(
            @PathVariable String ontoId,
            @RequestParam String classUri) {
        
        if (ontoId == null || ontoId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la ontología es requerido");
        }
        
        if (classUri == null || classUri.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El URI de la clase es requerido");
        }
        
        return configurationUseCase.getFormToModify(ontoId, classUri);
    }

    @PostMapping("/ontology")
    public OntologyCreateResponse createOntology(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("ontologyName") String ontologyName) {

        String fileName = multipartFile.getOriginalFilename();

        if (fileName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del archivo de la ontología es requerido");
        }

        if (multipartFile.getSize() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no debe ser vacío");
        }

        if (ontologyName == null || ontologyName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la ontología es requerido");
        }

        byte[] fileContent;
        try {
            fileContent = multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo obtener el contenido de la ontologìa", e);
        }

        String ontologyId = configurationUseCase.createOntology(fileName, fileContent, ontologyName);
        return new OntologyCreateResponse(ontologyId, ontologyName);
    }

    @PostMapping("/ontologies/{ontoId}/configurations/calculated-properties")
    @ResponseStatus(HttpStatus.OK)
    public void postOntologyCalculatedPropertyConfig(
            @PathVariable String ontoId,
            @RequestBody CalculatedPropertyConfigDescriptor calculatedPropertyConfigDescriptor) {

        if (ontoId == null || ontoId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la ontología es requerido");
        }

        if (calculatedPropertyConfigDescriptor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La configuración de propiedad calculada es requerida");
        }

        if (calculatedPropertyConfigDescriptor.propUri() == null || calculatedPropertyConfigDescriptor.propUri().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El URI de la propiedad es requerido");
        }

        if (calculatedPropertyConfigDescriptor.mainClass() == null || calculatedPropertyConfigDescriptor.mainClass().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clase principal es requerida");
        }

        configurationUseCase.postFormCalculatedPropertyConfig(ontoId, calculatedPropertyConfigDescriptor);
    }

    @DeleteMapping("/ontologies/{ontoId}/configurations/calculated-properties")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOntologyCalculatedPropertyConfig(
            @PathVariable String ontoId,
            @RequestBody CalculatedPropertyConfigDescriptor calculatedPropertyConfigDescriptor) {

        if (ontoId == null || ontoId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la ontología es requerido");
        }

        if (calculatedPropertyConfigDescriptor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La configuración de propiedad calculada es requerida");
        }

        if (calculatedPropertyConfigDescriptor.propUri() == null || calculatedPropertyConfigDescriptor.propUri().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El URI de la propiedad es requerido");
        }

        if (calculatedPropertyConfigDescriptor.mainClass() == null || calculatedPropertyConfigDescriptor.mainClass().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clase principal es requerida");
        }

        configurationUseCase.deleteFormCalculatedPropertyConfig(ontoId, calculatedPropertyConfigDescriptor);
    }

    @PostMapping("/ontologies/{ontoId}/configurations/artifice-classes")
    @ResponseStatus(HttpStatus.OK)
    public void postOntologyArtificeClassConfig(
            @PathVariable String ontoId,
            @RequestBody ArtificeClassConfigDescriptor artificeClassConfigDescriptor) {

        if (ontoId == null || ontoId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la ontología es requerido");
        }

        if (artificeClassConfigDescriptor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La configuración de clase artifice es requerida");
        }

        if (artificeClassConfigDescriptor.classUri() == null || artificeClassConfigDescriptor.classUri().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El URI de la clase artifice es requerido");
        }

        if (artificeClassConfigDescriptor.mainClassUri() == null || artificeClassConfigDescriptor.mainClassUri().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El URI de la clase principal es requerido");
        }

        configurationUseCase.postFormArtificeClassConfig(ontoId, artificeClassConfigDescriptor);
    }

    @DeleteMapping("/ontologies/{ontoId}/configurations/artifice-classes")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOntologyArtificeClassConfig(
            @PathVariable String ontoId,
            @RequestBody ArtificeClassConfigDescriptor artificeClassConfigDescriptor) {

        if (ontoId == null || ontoId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la ontología es requerido");
        }

        if (artificeClassConfigDescriptor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La configuración de clase artifice es requerida");
        }

        if (artificeClassConfigDescriptor.classUri() == null || artificeClassConfigDescriptor.classUri().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El URI de la clase artifice es requerido");
        }

        if (artificeClassConfigDescriptor.mainClassUri() == null || artificeClassConfigDescriptor.mainClassUri().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El URI de la clase principal es requerido");
        }

        configurationUseCase.deleteFormArtificeClassConfig(ontoId, artificeClassConfigDescriptor);
    }
}
