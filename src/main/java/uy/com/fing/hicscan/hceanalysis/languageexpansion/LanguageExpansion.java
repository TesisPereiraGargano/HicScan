package uy.com.fing.hicscan.hceanalysis.languageexpansion;

import java.util.Map;

public interface LanguageExpansion {
    /**
     * Extrae los principios activos o drogas de un medicamento dado su nombre comercial
     * @param inputText Texto a analizar
     * @return lista con los medicamentos extraídos
     */
    Map<String, String> obtenerPrincipiosActivos(String inputText);
}
