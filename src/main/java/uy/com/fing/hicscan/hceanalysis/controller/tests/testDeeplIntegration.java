package uy.com.fing.hicscan.hceanalysis.controller.tests;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/deepl"})
public class testDeeplIntegration {
    private String authKey = "528bdd79-3a4c-45ef-95e6-29175c5bc4aa:fx";

    public testDeeplIntegration() {
    }

    @GetMapping({"/translateText"})
    public ResponseEntity<String> translateText(@RequestParam String text) {
        try {
            Translator translator = new Translator(this.authKey);
            TextResult result = translator.translateText(text, (String)null, "en-US");
            return ResponseEntity.ok(result.getText());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}