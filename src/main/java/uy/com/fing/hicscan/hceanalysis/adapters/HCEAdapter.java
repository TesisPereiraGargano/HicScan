package uy.com.fing.hicscan.hceanalysis.adapters;

import uy.com.fing.hicscan.hceanalysis.dto.Autor;
import uy.com.fing.hicscan.hceanalysis.dto.Observacion;
import uy.com.fing.hicscan.hceanalysis.dto.Paciente;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface HCEAdapter {
    void parse(File file) throws IOException;
    Paciente getPaciente();
    Autor getAutor();
    String getTextoLibre();
    List<SustanciaAdministrada> getMedicamentos();
    List<Observacion> getObservaciones();

}

