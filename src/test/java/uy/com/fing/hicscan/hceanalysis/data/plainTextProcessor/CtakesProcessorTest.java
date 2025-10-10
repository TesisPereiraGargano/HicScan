package uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.impl.ctakes.CtakesProcessor;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;

import java.util.List;
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
            List<SustanciaAdministrada> meds = ctakesProcessor.extractDrugs(input);
            assertNotNull(meds, "Los medicamentos no pueden ser nulos");
            assertFalse(meds.isEmpty(), "La traducción no debe estar vacía");
            //assertEquals("Tamoxifen", meds.get(0).getDrugsCodes().("C0039286"));
        }

        @Test
        void testExtractDrugs2(){
        String input = "hypertensiom theodur 200 mg twice a day, proventil inhaler 2 puffs qid prn, prednisone 20mg once daily , hctz 25mg once a day theodur 200 mg twice a day inhaler proventil 2 inhalations qid prn prednisone 20mg once a day hctz 25mg once a day penicillin - urticaria , acetylsalicylic acid 500mg tablet - wheezing, codeine - itching and nausea father suffered a fatal myocardial infarction when he was about 50 years old., no cancer or diabetes. smoker: 1 ppd between the ages of 20 and 25, then quit.";
        List<SustanciaAdministrada>  meds = ctakesProcessor.extractDrugs(input);
        assertNotNull(meds, "Los medicamentos no pueden ser nulos");
        assertFalse(meds.isEmpty(), "La traducción no debe estar vacía");
        //assertEquals("Tamoxifen", meds.get("C0039286"));
    }
}







