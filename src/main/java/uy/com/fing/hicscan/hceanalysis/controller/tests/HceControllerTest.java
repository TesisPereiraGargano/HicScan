package uy.com.fing.hicscan.hceanalysis.controller.tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uy.com.fing.hicscan.hceanalysis.dto.PacienteExtendido;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;
import uy.com.fing.hicscan.hceanalysis.usecases.ProcessHceUseCase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/hce")
public class HceControllerTest {

    @Autowired
    private ProcessHceUseCase processHceUseCase;

    @PostMapping(value = "/process/estructurados", consumes = "application/xml")
    public ResponseEntity<String> processEstructurado(@RequestBody String xmlContent) throws IOException {
        // Guarda el XML como archivo temporal
        File tempFile = null;
        try {
            // Crear archivo temporal
            tempFile = File.createTempFile("hce-temp", ".xml");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(xmlContent);
            }

            // Ejecuta el use case
            try {
                Map<String, SustanciaAdministrada> res = processHceUseCase.obtenerMedicamentosEstructuradosHCE(tempFile);
                PacienteExtendido paciente = processHceUseCase.obtenerDatosPaciente(tempFile);
                return ResponseEntity.ok(String.format(
                        "Procesado correctamente: la info del paciente es %s, %s, %d, %s%s, %s%s. Los medicamentos son: %s",
                        paciente.getNombre(),
                        paciente.getFechaNacimiento(),
                        paciente.getEdad(),
                        paciente.getAlturaValor(),
                        paciente.getAlturaUnidad(),
                        paciente.getPesoValor(),
                        paciente.getPesoUnidad(),
                        res.keySet()
                ));

            }
            catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error ejecutando el use case: " + e.getMessage());
        }
    } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/process/textoLibre", consumes = "application/xml")
    public ResponseEntity<String> processTextoLibre(@RequestBody String xmlContent) throws IOException {
        // Guarda el XML como archivo temporal
        File tempFile = null;
        try {
            // Crear archivo temporal
            tempFile = File.createTempFile("hce-temp", ".xml");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(xmlContent);
            }

            // Ejecuta el use case
            try {
                Map<String, SustanciaAdministrada> res = processHceUseCase.obtenerMedicamentosTextoLibreHCE(tempFile);
                PacienteExtendido paciente = processHceUseCase.obtenerDatosPaciente(tempFile);
                return ResponseEntity.ok(String.format(
                        "Procesado correctamente: la info del paciente es %s, %s, %d, %s%s, %s%s. Los medicamentos son: %s",
                        paciente.getNombre(),
                        paciente.getFechaNacimiento(),
                        paciente.getEdad(),
                        paciente.getAlturaValor(),
                        paciente.getAlturaUnidad(),
                        paciente.getPesoValor(),
                        paciente.getPesoUnidad(),
                        res.keySet()
                ));

            }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error ejecutando el use case: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}