package uy.com.fing.hicscan.hceanalysis.languageexpansion;
import lombok.Getter;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Component;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.AbstractMap;
import java.util.List;

@Component
public class AhoCorasick {

    @Getter
    private final Trie arbolMedicamentos;

    public AhoCorasick(MedicationDictionary medicationDictionary) throws IOException, ParserConfigurationException {
        List<AbstractMap.SimpleEntry<String, String>> medicamentos = medicationDictionary.getListaNombresMedicamentos();

        Trie.TrieBuilder builder = Trie.builder().onlyWholeWords();
        for (AbstractMap.SimpleEntry<String, String> med : medicamentos) {
            builder.addKeyword(med.getValue());
        }

        arbolMedicamentos = builder.build();
    }
}
