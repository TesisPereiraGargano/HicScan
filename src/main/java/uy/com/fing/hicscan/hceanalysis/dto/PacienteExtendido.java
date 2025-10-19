package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;

import static uy.com.fing.hicscan.hceanalysis.utils.FunctionUtils.calcularEdad;

/**
 * Representa información extendida de un paciente en el contexto de una Historia Clínica Electrónica (HCE).
 *
 * Incluye todos los campos del {@link Paciente} básico más medidas físicas
 * como altura y peso junto con sus unidades respectivas.
 *
 * Esta clase permite modelar un perfil clínico más completo para análisis y visualización médica.
 *
 */
@Getter
public class PacienteExtendido {

    private String nombre;
    private String genero;
    private String fechaNacimiento;
    private int edad;
    private String estadoCivil; //maritalStatusCode
    private String raza; //raceCode
    private String lugarNacimiento; //birthplace
    private String alturaValor;
    private String alturaUnidad;
    private String pesoValor;
    private String pesoUnidad;


    public PacienteExtendido(String nombre, String genero, String fechaNacimiento, String estadoCivil, String raza, String lugarNacimiento, String alturaValor, String alturaUnidad, String pesoValor, String pesoUnidad){
        this.nombre = nombre;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
        this.estadoCivil = estadoCivil;
        this.raza = raza;
        this.lugarNacimiento = lugarNacimiento;
        this.alturaValor = alturaValor;
        this.alturaUnidad = alturaUnidad;
        this.pesoValor = pesoValor;
        this.pesoUnidad = pesoUnidad;
    }
    //edad
    public int getEdad() {
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            return calcularEdad(fechaNacimiento);
        }
        return -1; // Valor por defecto si no hay fecha válida
    }
}
