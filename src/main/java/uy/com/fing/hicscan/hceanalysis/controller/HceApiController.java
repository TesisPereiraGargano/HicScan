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

    @GetMapping("/processHCE")
    public ApiResponse processHCE(
            @RequestParam String inputText
    ){
        List<SustanciaAdministrada> meds = processHceUseCase.processPlainTextHCE(inputText);
        //return new ApiResponse("success", "Datos extraídos correctamente.", meds); --TO DO CORREGIR
        return new ApiResponse("success", "Datos extraídos correctamente.", new HashMap<>());

    }

    @PostMapping("/obtenerDatosPacienteBasico")
    public ResponseEntity<Object> obtenerDatosPacienteBasico(@RequestBody String idPaciente) {
        PacienteExtendido paciente = processHceUseCase.obtenerDatosPaciente(idPaciente);
        if(paciente == null){
            //significa que no encontró la hce
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El paciente con id " + idPaciente + " no tiene HCE cargada en el sistema.");
        }
        return ResponseEntity.ok(paciente);
    }

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
