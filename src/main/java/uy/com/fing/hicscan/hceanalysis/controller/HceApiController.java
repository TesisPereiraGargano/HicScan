package uy.com.fing.hicscan.hceanalysis.controller;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.fing.hicscan.hceanalysis.dto.*;
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
    /**
     * Request DTO simplificado que recibe solo los datos de la mujer, y un identificador
     */
    @Getter
    @Setter
    public static class CompleteWomanProcessingRequest {
        private Map<String, String> womanHistoryData = new HashMap<>();
        private String id;

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
    @GetMapping("/obtenerDatosPacienteBasico")
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
    @GetMapping("/obtenerDatosPacienteExtendido")
    public ResponseEntity<Object> obtenerDatosPacienteExtendido(@RequestParam String idPaciente) {
        DatosHCE datosExtraidos = processHceUseCase.obtenerDatosPacienteExtendido(idPaciente);
        if(datosExtraidos == null){
            //significa que no encontró la hce
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El paciente con id " + idPaciente + " no tiene HCE cargada en el sistema.");
        }
        return ResponseEntity.ok(datosExtraidos);
    }


    /**
     * Obtiene toda la información asociada al paciente y las recomendaciones brindadas al mismo.
     *
     * @param request que tiene la info necesaria para hacer el procesamiento que es un id de paciente con HCE cargada
     *                en el sistema y el mapa de la información obtenida del cuestionario.
     * @return {@link ResponseEntity} con estado OK y datos extendidos o NOT FOUND si no existe.
     */
    @GetMapping("/obtenerDatosPacienteExtendidoConRecomendaciones")
    public ResponseEntity<Object> obtenerDatosPacienteExtendidoConRecomendaciones(@RequestBody CompleteWomanProcessingRequest request) {
        PacienteRecomendacion datosExtraidos = processHceUseCase.obtenerDatosPacienteExtendidoConRecomendaciones(request.getId(), request.getWomanHistoryData());
        if(datosExtraidos == null){
            //significa que no encontró la hce
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El paciente con id " + request.getId() + " no tiene HCE cargada en el sistema.");
        }
        return ResponseEntity.ok(datosExtraidos);
    }

    /**
     * Obtiene los datos básicos de todos los pacientes con HCE cargada en el sistema.
     *
     * @return {@link ResponseEntity} con estado OK y lista de pacientes o lista vacía si no hay pacientes.
     */
    @GetMapping("/obtenerTodosLosPacientesBasicos")
    public ResponseEntity<List<PacienteExtendido>> obtenerTodosLosPacientesBasicos() {
        List<PacienteExtendido> pacientes = processHceUseCase.obtenerTodosLosPacientesBasicos();
        return ResponseEntity.ok(pacientes);
    }

}
