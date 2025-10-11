package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class DatosHCE {
    PacienteExtendido datosBasicosPaciente;
    Medicamentos medicamentos;

    @Getter
    @Setter
    public static class Medicamentos {
        private Clasificados clasificados;
        private List<SustanciaAdministrada> noClasificados;

        @Getter
        @Setter
        public static class Clasificados {
            private List<SustanciaAdministrada> diureticos;
            private List<SustanciaAdministrada> noDiureticos;
        }
    }
}
