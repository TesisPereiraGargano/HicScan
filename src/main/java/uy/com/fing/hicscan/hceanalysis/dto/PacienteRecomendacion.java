package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
import lombok.Setter;
import uy.com.fing.hicscan.hceanalysis.data.ontologyRepository.ReasoningResult;

@Getter
@Setter
public class PacienteRecomendacion {
    private DatosHCE datosPaciente;
    private ReasoningResult reasoningResult;

    public PacienteRecomendacion(DatosHCE paciente, ReasoningResult razonamiento){
        this.datosPaciente = paciente;
        this.reasoningResult = razonamiento;

    }
}


