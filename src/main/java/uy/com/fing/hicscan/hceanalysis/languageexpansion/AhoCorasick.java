package uy.com.fing.hicscan.hceanalysis.languageexpansion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Component;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.AbstractMap;
import java.util.List;

@Slf4j
@Component
public class AhoCorasick {

    @Getter
    private final Trie arbolMedicamentos;

    public AhoCorasick(MedicationDictionary medicationDictionary) throws IOException, ParserConfigurationException {
        List<AbstractMap.SimpleEntry<String, String>> medicamentos = medicationDictionary.getListaNombresMedicamentos();

        Trie.TrieBuilder builder = Trie.builder().onlyWholeWords();
        for (AbstractMap.SimpleEntry<String, String> med : medicamentos) {
            builder.addKeyword(med.getKey().toLowerCase());
        }
        arbolMedicamentos = builder.build();

        // Log de las keywords agregadas
        //log.info("Se cargaron {} medicamentos en el trie: {}", medicamentos.size(),medicamentos.stream().map(AbstractMap.SimpleEntry::getValue).toList());
        log.info("Se cargaron {} medicamentos en el trie", medicamentos.size());
    }
    }
