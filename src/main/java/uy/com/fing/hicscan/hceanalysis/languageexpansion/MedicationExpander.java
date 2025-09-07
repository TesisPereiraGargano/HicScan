package uy.com.fing.hicscan.hceanalysis.languageexpansion;

import com.google.common.collect.BiMap;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uy.com.fing.hicscan.hceanalysis.data.ctakes.dto.ApiResponse;

import java.text.Normalizer;
import java.util.*;

/*Este módulo provee un API que va a permitir utilizar el diccionario de medicamentos disponible en la web de AGESIC
* para poder obtener aquellos medicamentos que se le recetaron al paciente con su nombre comercial
* y no escribiendo o describiendo la droga del mismo.*/
@Slf4j
@RestController
@RequestMapping("/medExpander")
public class MedicationExpander {
    private final AhoCorasick ahoCorasick;
    private final MedicationDictionary medicationDictionary;


    public MedicationExpander(AhoCorasick ahoCorasick, MedicationDictionary medicationDictionary) {
        this.ahoCorasick = ahoCorasick;
        this.medicationDictionary = medicationDictionary;
    }


    @PostMapping("/getDrugsFromComercialName")
    public ResponseEntity<ApiResponse> obtenerPrincipiosActivos(
            @RequestParam String inputText
    ){
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

            log.info("El texto normalizado es {}",textoNormalizado);

            // parsea el texto y obtiene coincidencias
            for (Emit emit : arbolMedicamentos.parseText(textoNormalizado)) {
                encontrados.add(emit.getKeyword());
            }

            log.info("Se obtuvieron las siguientes coincidencias: {}", encontrados);
            //Busco el principio activo

            //lo casteo sólo para probar
            Map<String,String> res = new HashMap<>();

            BiMap<String, String> listaNombresMedicamentos = medicationDictionary.getListaNombresMedicamentos();
            HashMap<String, String> listaVMPid = medicationDictionary.getListaVMPid();
            HashMap<String, String> listaPrincipiosActivos = medicationDictionary.getListaPrincipiosActivos();

            for (String med : encontrados){
                //AMP_id
                String amp_id = listaNombresMedicamentos.inverse().get(med);
                log.info("amp_id {}: {}", med, amp_id);
                //VMP_id
                String vmp_id = listaVMPid.get(amp_id);
                log.info("vmp_id {}", vmp_id);
                //Nombre del principio activo
                String nomPrincActivo = listaPrincipiosActivos.get(vmp_id);
                log.info("nomPrincActivo {}", nomPrincActivo);
                res.put(med,nomPrincActivo);
            }

            ApiResponse respuesta = new ApiResponse("success", "Los medicamentos y su traduccion a principios activos es", res);
            return ResponseEntity.status(200).body(respuesta);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse respuesta = new ApiResponse("error", "Ocurrió un error inesperado: " + e.getMessage(), new HashMap<>());
            return ResponseEntity.status(500).body(respuesta);
        }


    }
}
