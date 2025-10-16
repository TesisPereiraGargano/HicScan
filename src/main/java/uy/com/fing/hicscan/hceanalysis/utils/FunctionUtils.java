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
    /**
     Limpia un string eliminando saltos de línea, tabulaciones y múltiples espacios consecutivos.
     Por ejemplo, transforma "Juan\n Perez" en "Juan Perez".
     @param texto El texto original del nombre que puede contener saltos de línea y espacios adicionales.
     @return Una cadena limpia, con espacios uniformes y sin saltos de línea ni espacios extras al inicio o final, y
             retorna null si la entrada es null.
     */
    public static String limpiarString(String texto) {
        if (texto == null) {
            return null;
        }
        // Reemplaza cualquier secuencia de espacios en blanco (incluye saltos de línea, tabs) por un solo espacio
        String limpio = texto.replaceAll("\\s+", " ").trim();
        return limpio;
    }

}
