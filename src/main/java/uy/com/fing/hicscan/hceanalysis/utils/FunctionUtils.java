package uy.com.fing.hicscan.hceanalysis.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class FunctionUtils {
    /**
     * Calcula la edad a partir de un string de fecha de nacimiento con formato YYYYMMDD.
     */
    public static int calcularEdad(String fechaNacimientoStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr, formatter);
        LocalDate hoy = LocalDate.now();
        return Period.between(fechaNacimiento, hoy).getYears();
    }

}
