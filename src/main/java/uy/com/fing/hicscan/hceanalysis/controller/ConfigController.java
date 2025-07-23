package uy.com.fing.hicscan.hceanalysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import uy.com.fing.hicscan.hceanalysis.usecases.ConfigurationUseCase;

import java.io.IOException;

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
    public String[] getOntologies() {
        return configurationUseCase.getOntologies();
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
}
