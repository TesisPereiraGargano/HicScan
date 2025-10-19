package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
/**
 * Representa una observación médica registrada en la Historia Clínica Electrónica (HCE).
 *
 * Esta clase encapsula los datos básicos de una observación, incluyendo el código normalizado,
 * sistema de codificación, estado, momento de efectividad y magnitud con su unidad asociada.
 *
 * Se utiliza para modelar datos clínicos cuantitativos o cualitativos relevantes para el análisis.
 *
 */
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
