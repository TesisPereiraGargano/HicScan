package uy.com.fing.hicscan.hceanalysis.data.translator;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import uy.com.fing.hicscan.hceanalysis.data.translator.impl.DeeplTranslator;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {
        "deepl.api.key=528bdd79-3a4c-45ef-95e6-29175c5bc4aa:fx"
})
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
