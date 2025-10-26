package uy.com.fing.hicscan.hceanalysis.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.text.Normalizer;


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
    /**
     Conviente un string a formato camel case. Por ejemplo, transforma "Hydrochlorothiazide 25mg tablet"
     en "hydrochlorothiazide25mgTablet"
     @param texto El texto original que se quiere convertir a camel case eliminando espacios y caracteres especiales.
     @return Una cadena limpia, en formato camelCase.
     */
    public static String toCamelCase(String texto) {
            texto = Normalizer.normalize(texto, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", ""); // quita acentos

            texto = texto.replaceAll("[^A-Za-z0-9]+", " ").trim();

            StringBuilder resultado = new StringBuilder();
            for (String palabra : texto.split("\\s+")) {
                if (!palabra.isEmpty()) {
                    resultado.append(
                            Character.toUpperCase(palabra.charAt(0))
                    );
                    if (palabra.length() > 1) {
                        resultado.append(palabra.substring(1).toLowerCase());
                    }
                }
            }
        String resultadoFinal = resultado.toString();
        return resultadoFinal.isEmpty()
                ? resultadoFinal
                : Character.toLowerCase(resultadoFinal.charAt(0)) + resultadoFinal.substring(1);
        }

}
