package uy.com.fing.hicscan.hceanalysis.dto;

import ch.qos.logback.core.joran.sanity.Pair;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class SustanciaAdministrada {
    String doseQuantityValue;
    String doseQuantityUnit;
    String periodAdministrationValue;
    String periodAdministrationUnit;
    //entiendo que podría tener referenciada mas de una droga
    List<Map.Entry<String,String>> drugsCodes; //es pareja de codigo y diccionario (ej <5, SNOMED CT>);

    public SustanciaAdministrada (String doseQuantityUnit, String doseQuantityValue, String periodAdministrationValue, String periodAdministrationUnit, List<Map.Entry<String,String>> drugs){
        this.doseQuantityValue = doseQuantityValue;
        this.doseQuantityUnit = doseQuantityUnit;
        this.periodAdministrationValue = periodAdministrationValue;
        this.periodAdministrationUnit = periodAdministrationUnit;
        this.drugsCodes = drugs;
    }

}
