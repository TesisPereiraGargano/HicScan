package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SustanciaAdministrada {
    String name;
    String doseQuantityValue;
    String doseQuantityUnit;
    String periodAdministrationValue;
    String periodAdministrationUnit;
    //entiendo que podría tener referenciada mas de una droga
    List<Droga> drugs; // Cada droga con sus códigos asociados en algún diccionario y su nombre

    public SustanciaAdministrada (String name, String doseQuantityUnit, String doseQuantityValue, String periodAdministrationValue, String periodAdministrationUnit, List<Droga> drugs){
        this.name = name;
        this.doseQuantityValue = doseQuantityValue;
        this.doseQuantityUnit = doseQuantityUnit;
        this.periodAdministrationValue = periodAdministrationValue;
        this.periodAdministrationUnit = periodAdministrationUnit;
        this.drugs = drugs;
    }

}
