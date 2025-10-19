package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;

import static uy.com.fing.hicscan.hceanalysis.utils.FunctionUtils.calcularEdad;

/**
 * Representa los datos demográficos fundamentales de un paciente dentro del sistema HicScan.
 *
 * Incluye información personal como nombre, género, fecha de nacimiento, estado civil,
 * raza y lugar de nacimiento.
 *
 * La edad se calcula dinámicamente a partir de la fecha de nacimiento.
 */

@Getter
public class Paciente {

    private String nombre;
    private String genero;
    private String fechaNacimiento;
    private int edad;
    private String estadoCivil; //maritalStatusCode
    private String raza; //raceCode
    private String lugarNacimiento; //birthplace

    public Paciente(String nombre, String genero, String fechaNacimiento, String estadoCivil, String raza, String lugarNacimiento){
        this.nombre = nombre;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
        this.estadoCivil = estadoCivil;
        this.raza = raza;
        this.lugarNacimiento = lugarNacimiento;
    }
    //edad
    public int getEdad() {
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            return calcularEdad(fechaNacimiento);
        }
        return -1; // Valor por defecto si no hay fecha válida
    }

}
