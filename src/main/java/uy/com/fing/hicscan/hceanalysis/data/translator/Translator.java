package uy.com.fing.hicscan.hceanalysis.data.translator;

public interface Translator {
    /**
     * Traduce un texto de un idioma a otro.
     *
     * @param text      Texto a traducir
     * @param sourceLang Idioma origen (ej. "ES")
     * @param targetLang Idioma destino (ej. "EN")
     * @return Texto traducido
     */
    String translate(String text, String sourceLang, String targetLang);
}
