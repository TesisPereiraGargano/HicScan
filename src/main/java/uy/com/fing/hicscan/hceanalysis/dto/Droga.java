package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa una sustancia medicinal, incluyendo sus códigos en diferentes
 * diccionarios o sistemas de ontologías clínicas/médicas.
 *
 * La clase contiene los códigos de SNOMED CT, RxNorm y CUI, permitiendo interoperabilidad
 * entre sistemas que utilizan estas ontologías para identificar medicamentos y sustancias.
 *
 */
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

