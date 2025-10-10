package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
import lombok.Setter;

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
