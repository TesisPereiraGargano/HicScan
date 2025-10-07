package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;

import static uy.com.fing.hicscan.hceanalysis.utils.FunctionUtils.calcularEdad;

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
