package uy.com.fing.hicscan.hceanalysis.data.translator;

import org.junit.jupiter.api.Test;
import uy.com.fing.hicscan.hceanalysis.data.translator.impl.DeeplTranslator;

import static org.junit.jupiter.api.Assertions.*;

class DeeplTranslatorTest {

    private final Translator translator = new DeeplTranslator(); // Usa tu implementación real

    @Test
    void testTranslation() {
        String input = "Hola mundo";
        String translated = translator.translate(input, "ES", "EN");
        assertNotNull(translated, "La traducción no debe ser nula");
        assertFalse(translated.isEmpty(), "La traducción no debe estar vacía");
        assertEquals("Hello world", translated);
    }
}
