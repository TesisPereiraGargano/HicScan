package uy.com.fing.hicscan.hceanalysis.adapters.impl;

import lombok.Getter;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapter;
import uy.com.fing.hicscan.hceanalysis.dto.Autor;
import uy.com.fing.hicscan.hceanalysis.dto.Observacion;
import uy.com.fing.hicscan.hceanalysis.dto.Paciente;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;

import java.io.IOException;
import java.io.File;
import java.util.List;

@Getter
public class FHIR_Adapter implements HCEAdapter {
    private Paciente paciente;
    private Autor autor;
    private String textoLibre;
    private List<SustanciaAdministrada> medicamentos;
    private List<Observacion> observaciones;

    @Override
    public void parse(File file) throws IOException {
        System.out.println("Parseando archivo FHIR: " + file.getName());
        // Aquí iría la lógica específica de FHIR
    }
}