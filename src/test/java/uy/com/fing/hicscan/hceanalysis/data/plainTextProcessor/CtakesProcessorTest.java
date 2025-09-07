package uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.impl.ctakes.CtakesProcessor;

import java.util.Map;

@SpringBootTest
@TestPropertySource(properties = {
        "umls.key=9acb4127-e18e-4a0c-a53d-6555dd08fb32"
})
public class CtakesProcessorTest {

        @Autowired
        private CtakesProcessor ctakesProcessor;

        @Test
        void testTranslation() {
            String input = "The patient presented with a palpable breast mass and was prescribed a mammogram and Tamoxifen for further evaluation.";
            Map<String, String> meds = ctakesProcessor.extractDrugs(input);
            assertNotNull(meds, "Los medicamentos no pueden ser nulos");
            assertFalse(meds.isEmpty(), "La traducción no debe estar vacía");
            assertEquals("Tamoxifen", meds.get("C0039286"));
        }
}






