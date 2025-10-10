package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Droga {
    private CodDiccionario codigos;
    private String nombre;

    public Droga(CodDiccionario codigos, String nombre) {
       this.codigos = codigos;
       this.nombre = nombre;
        }
    }

