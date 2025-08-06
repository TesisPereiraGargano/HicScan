package uy.com.fing.hicscan.hceanalysis.languageexpansion;

import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uy.com.fing.hicscan.hceanalysis.data.ctakes.dto.ApiResponse;

import java.util.*;

/*Este módulo provee un API que va a permitir utilizar el diccionario de medicamentos disponible en la web de AGESIC
* para poder obtener aquellos medicamentos que se le recetaron al paciente con su nombre comercial
* y no escribiendo o describiendo la droga del mismo.*/
@Slf4j
@RestController
@RequestMapping("/medExpander")
public class MedicationExpander {
    private final AhoCorasick ahoCorasick;

    public MedicationExpander(AhoCorasick ahoCorasick) {
        this.ahoCorasick = ahoCorasick;
    }

    @PostMapping("/getDrugsFromComercialName")
    public ResponseEntity<ApiResponse> obtenerPrincipiosActivos(
            @RequestParam String inputText
    ){
        try {
            Trie arbolMedicamentos = this.ahoCorasick.getArbolMedicamentos();
            log.info("El arbolMedicamentos es: {}", arbolMedicamentos);
            Set<String> encontrados = new HashSet<>();

            // parsea el texto y obtiene coincidencias
            for (Emit emit : arbolMedicamentos.parseText(inputText.toLowerCase())) {
                encontrados.add(emit.getKeyword());
            }

            log.info("Se obtuvieron las siguientes coincidencias: {}", encontrados);
            //Busco el principio activo
            //lo casteo sólo para probar
            Map<String,String> res = new HashMap<>();

            for (String med : encontrados){
                res.put(med,"dummy");
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
