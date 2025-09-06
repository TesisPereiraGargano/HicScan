package uy.com.fing.hicscan.hceanalysis.data.translator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uy.com.fing.hicscan.hceanalysis.data.translator.impl.DeeplTranslator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "deepl.api.key=528bdd79-3a4c-45ef-95e6-29175c5bc4aa:fx"
})
class DeeplTranslatorTest {

    @Autowired
    private DeeplTranslator deeplTranslator;  // ¡Spring inyecta el bean!

    @Test
    void testTranslation() {
        String input = "Hola mundo";
        String translated = deeplTranslator.translate(input, "ES", "en-US");
        assertNotNull(translated, "La traducción no debe ser nula");
        assertFalse(translated.isEmpty(), "La traducción no debe estar vacía");
        assertEquals("Hello world", translated);
    }
}
