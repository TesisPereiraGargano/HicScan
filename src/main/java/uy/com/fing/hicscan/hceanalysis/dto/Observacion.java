package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;

@Getter
public class Observacion
{
    String code;
    String codeSystem;
    String status;
    String effectiveTime;
    String meditionValue;
    String meditionUnit;

    public Observacion(String code, String codeSystem, String status, String effectiveTime, String meditionValue, String meditionUnit) {
        this.code = code;
        this.codeSystem = codeSystem;
        this.status = status;
        this.effectiveTime = effectiveTime;
        this.meditionUnit = meditionUnit;
        this.meditionValue = meditionValue;
    }
}
