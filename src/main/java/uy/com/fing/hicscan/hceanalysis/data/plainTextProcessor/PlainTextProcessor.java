package uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor;

import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;

import java.util.List;
import java.util.Map;

public interface PlainTextProcessor {
    /**
     * Extrae los medicamentos de un texto usando una herramienta de analisis de texto plano
     * @param inputText Texto a analizar
     * @return lista con los medicamentos extraídos
     */
    List<SustanciaAdministrada> extractDrugs(String inputText);
}
