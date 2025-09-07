package uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor;

import java.util.Map;

public interface PlainTextProcessor {
    /**
     * Extrae los medicamentos de un texto usando una herramienta de analisis de texto plano
     * @param inputText Texto a analizar
     * @return lista con los medicamentos extraídos
     */
    Map<String, String> extractDrugs(String inputText);
}
