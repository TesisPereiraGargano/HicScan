package uy.com.fing.hicscan.hceanalysis.integration;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.PlainTextProcessor;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.impl.ctakes.dto.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CtakesServiceIntegrationTest {

    @Autowired
    private PlainTextProcessor textProcessor; // inyecta CtakesService real

    private String testText;

    @BeforeEach
    void setUp() {
        testText = "El paciente tomó Paracetamol 500mg y Amoxicilina 250mg durante la semana.";
    }

    @Test
    void testAnalyzeDrugs() throws ResourceInitializationException, AnalysisEngineProcessException {
        Map<String, String>  response = textProcessor.analyze(testText);

        assertNotNull(response);
        assertTrue(response.getMessage().contains("Datos extraídos correctamente."));

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());

        // Mostrar resultados por consola
        response.getData().forEach((cui, nombre) ->
            System.out.println("CUI: " + cui + " Nombre: " + nombre)
        );
    }
}
