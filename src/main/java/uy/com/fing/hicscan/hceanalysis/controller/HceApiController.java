package uy.com.fing.hicscan.hceanalysis.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.dto.ApiResponse;
import uy.com.fing.hicscan.hceanalysis.dto.DatosHCE;
import uy.com.fing.hicscan.hceanalysis.dto.PacienteExtendido;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;
import uy.com.fing.hicscan.hceanalysis.usecases.ProcessHceUseCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para exponer la API pública relacionada con el procesamiento
 * y consulta de Historias Clínicas Electrónicas (HCE).
 *
 * Ofrece endpoints para procesar texto libre, obtener datos básicos y extendidos
 * del paciente extraídos de las HCE almacenadas en memoria.
 *
 * Implementa rutas bajo la base {@code /HCE}.
 */
@RestController
@RequestMapping("/HCE")
public class HceApiController {
    private final ProcessHceUseCase processHceUseCase;
    private HceApiController(ProcessHceUseCase processHceUseCase){
        this.processHceUseCase = processHceUseCase;
    }

    @GetMapping("/")
    public String index() {
        return "Servidor funcionando correctamente.";
    }

    /**
     * Obtiene datos básicos extendidos del paciente a partir del ID de la HCE.
     *
     * @param idPaciente identificador del paciente para buscar la HCE almacenada.
     * @return {@link ResponseEntity} con estado OK y datos del paciente o con estado NOT FOUND si no existe.
     */
    @PostMapping("/obtenerDatosPacienteBasico")
    public ResponseEntity<Object> obtenerDatosPacienteBasico(@RequestBody String idPaciente) {
        PacienteExtendido paciente = processHceUseCase.obtenerDatosPaciente(idPaciente);
        if(paciente == null){
            //significa que no encontró la hce
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El paciente con id " + idPaciente + " no tiene HCE cargada en el sistema.");
        }
        return ResponseEntity.ok(paciente);
    }

    /**
     * Obtiene datos clínicos extendidos de la HCE, incluyendo medicamentos y observaciones.
     *
     * @param idPaciente identificador del paciente para buscar la HCE almacenada.
     * @return {@link ResponseEntity} con estado OK y datos extendidos o NOT FOUND si no existe.
     */
    @PostMapping("/obtenerDatosPacienteExtendido")
    public ResponseEntity<Object> obtenerDatosPacienteExtendido(@RequestBody String idPaciente) {
        DatosHCE datosExtraidos = processHceUseCase.obtenerDatosPacienteExtendido(idPaciente);
        if(datosExtraidos == null){
            //significa que no encontró la hce
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El paciente con id " + idPaciente + " no tiene HCE cargada en el sistema.");
        }
        return ResponseEntity.ok(datosExtraidos);
    }

}
