package uy.com.fing.hicscan.hceanalysis.languageexpansion;

import com.google.common.collect.BiMap;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;

@Slf4j
@Service
public class MedicationExpander implements LanguageExpansion {
    private final AhoCorasick ahoCorasick;
    private final MedicationDictionary medicationDictionary;


    public MedicationExpander(AhoCorasick ahoCorasick, MedicationDictionary medicationDictionary) {
        this.ahoCorasick = ahoCorasick;
        this.medicationDictionary = medicationDictionary;
    }

    @Override
    public Map<String, String> obtenerPrincipiosActivos(String inputText) {
        Map<String, String> res = new HashMap<>();
        try {
            Trie arbolMedicamentos = this.ahoCorasick.getArbolMedicamentos();
            log.info("El arbolMedicamentos es: {}", arbolMedicamentos.toString());
            Set<String> encontrados = new HashSet<>();

            String textoNormalizado = Normalizer.normalize(inputText, Normalizer.Form.NFD) //me separa las comillas si venia "ó" se convierte en "o´"
                    .replaceAll("\\p{M}", "") //elimina cualquier carácter Unicode que sea una marca (diacrítico) entran los tildes aca
                    .replaceAll("[^\\p{L}\\p{Nd}\\s]", " ")  //elimino cualquier cosa que no sea letra o numero
                    .replaceAll("\\s+", " ") //unifico espacios múltiples
                    .toLowerCase()
                    .trim(); //borro espacios al incicio o final

            log.info("El texto normalizado es {}", textoNormalizado);

            // parsea el texto y obtiene coincidencias
            for (Emit emit : arbolMedicamentos.parseText(textoNormalizado)) {
                encontrados.add(emit.getKeyword());
            }

            log.info("Se obtuvieron las siguientes coincidencias: {}", encontrados);
            //Busco el principio activo

            BiMap<String, String> listaNombresMedicamentos = medicationDictionary.getListaNombresMedicamentos();
            HashMap<String, String> listaVMPid = medicationDictionary.getListaVMPid();
            HashMap<String, String> listaPrincipiosActivos = medicationDictionary.getListaPrincipiosActivos();

            for (String med : encontrados) {
                //AMP_id
                String amp_id = listaNombresMedicamentos.inverse().get(med);
                log.info("amp_id {}: {}", med, amp_id);
                //VMP_id
                String vmp_id = listaVMPid.get(amp_id);
                log.info("vmp_id {}", vmp_id);
                //Nombre del principio activo
                String nomPrincActivo = listaPrincipiosActivos.get(vmp_id);
                log.info("nomPrincActivo {}", nomPrincActivo);
                res.put(med, nomPrincActivo);
            }

            return res;
        } catch (Exception e) {
            log.error("Error en la expansion de medicamentos");
            e.printStackTrace();
            return res;
        }

    }
}
