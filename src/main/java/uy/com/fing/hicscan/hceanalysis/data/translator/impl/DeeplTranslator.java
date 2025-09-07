package uy.com.fing.hicscan.hceanalysis.data.translator.impl;
import com.deepl.api.DeepLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.data.translator.Translator;

@Service
public class DeeplTranslator implements Translator {

    @Value("${deepl.api.key}") //se define en el archivo application.properties
    private String authKey;

    @Override
    public String translate(String text, String sourceLang, String targetLang) {
        com.deepl.api.Translator deepl = new com.deepl.api.Translator(authKey);
        try {
            return deepl.translateText(text, sourceLang, targetLang).getText();
        } catch (DeepLException | InterruptedException e) {
            throw new RuntimeException("Error al traducir el texto", e);
        }
    }

}
