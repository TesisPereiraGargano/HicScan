package uy.com.fing.hicscan.hceanalysis.data;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deepl")
public class DeeplTranslator {

    @Value("${deepl.api.key}") //se define en el archivo application.properties
    private String authKey;

    @GetMapping("/translateText")
    public ResponseEntity<String> translateText(@RequestParam String text) {
        try {
            Translator translator = new Translator(authKey);
            //se podría dejar como parámetro el language de forma que fuera configurable o algo así
            TextResult result = translator.translateText(text, null, "en-US");
            return ResponseEntity.ok(result.getText());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

}
