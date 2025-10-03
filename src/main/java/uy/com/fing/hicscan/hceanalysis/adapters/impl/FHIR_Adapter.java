package uy.com.fing.hicscan.hceanalysis.adapters.impl;

import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapter;
import java.io.IOException;
import java.io.File;

public class FHIR_Adapter implements HCEAdapter {
    @Override
    public void parse(File file) throws IOException {
        System.out.println("Parseando archivo FHIR: " + file.getName());
        // Aquí iría la lógica específica de FHIR
    }
}