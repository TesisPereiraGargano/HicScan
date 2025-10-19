package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO que modela los códigos asociados a una entidad clínica dentro de distintos
 * diccionarios, metatesauros o sistemas de referencia terminológica.
 *
 * Esta clase permite unificar las correspondencias entre los principales estándares
 * utilizados en el dominio biomédico:
 * <ul>
 *     <li>SNOMED CT: Nomenclatura sistematizada de medicina clínica.</li>
 *     <li>RxNorm: Terminología de referencia de medicamentos mantenida por la NLM.</li>
 *     <li>CUI: Identificador unificado dentro del Metatesauro de UMLS.</li>
 * </ul>
 *
 */
@Getter
@Setter
//DTO que modela el código de una entidad en los diferentes diccionarios/metatesauros/ontologías/etc
public class CodDiccionario {
    private String snomedCT;
    private String rxnorm;
    private String cui;

    public CodDiccionario() {
        this.snomedCT = "";
        this.rxnorm = "";
        this.cui = "";
    }
}
