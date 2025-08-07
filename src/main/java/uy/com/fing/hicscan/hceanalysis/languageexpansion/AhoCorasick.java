package uy.com.fing.hicscan.hceanalysis.languageexpansion;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Component;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.Normalizer;
import java.util.Map;

@Slf4j
@Component
public class AhoCorasick {

    @Getter
    private final Trie arbolMedicamentos;

    public AhoCorasick(MedicationDictionary medicationDictionary) throws IOException, ParserConfigurationException {
        BiMap<String, String> medicamentos = medicationDictionary.getListaNombresMedicamentos();

        Trie.TrieBuilder builder = Trie.builder().onlyWholeWords();
        for (Map.Entry<String, String> med : medicamentos.entrySet()) {
            String keyword = Normalizer.normalize(med.getValue(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replaceAll("[^\\p{L}\\p{Nd}\\s]", " ")
                    .replaceAll("\\s+", " ")
                    .toLowerCase()
                    .trim();
            builder.addKeyword(keyword);
        }
        arbolMedicamentos = builder.build();

        // Log de las keywords agregadas
        //log.info("Se cargaron {} medicamentos en el trie: {}", medicamentos.size(),medicamentos.stream().map(AbstractMap.SimpleEntry::getValue).toList());
        log.info("Se cargaron {} medicamentos en el trie", medicamentos.size());
    }
    }
