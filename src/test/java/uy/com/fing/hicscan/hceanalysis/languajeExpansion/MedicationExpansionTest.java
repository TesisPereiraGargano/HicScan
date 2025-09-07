package uy.com.fing.hicscan.hceanalysis.languajeExpansion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import uy.com.fing.hicscan.hceanalysis.languageexpansion.MedicationExpander;

import java.util.Map;

@SpringBootTest

public class MedicationExpansionTest {

    @Autowired
    private MedicationExpander medExpander;

    @Test
    void testObtenerPrincipiosActivos() {
        String input = "El paciente recibió novemina, novemina, amóxidal 250. ";
        Map<String, String> principiosActs = medExpander.obtenerPrincipiosActivos(input);
        assertNotNull(principiosActs, "El input tenía principios activos, no puede dar null");
        assertFalse(principiosActs.isEmpty(), "El input tiene medicamentos por lo que no debe estar vacío el map del response");
        assertEquals("amoxicilina 250mg comprimido", principiosActs.get("amoxidal 250"));
        assertEquals("dipirona 1g 2ml 500mg ml inyectable", principiosActs.get("novemina"));
    }
}