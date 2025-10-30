package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;

/**
 * Representa información básica de un paciente en el contexto de una Historia Clínica Electrónica (HCE).
 *
 * Incluye el identificador del paciente junto con todos los campos del {@link PacienteExtendido}.
 * Esta clase se utiliza específicamente para el endpoint que obtiene todos los pacientes básicos.
 *
 */
@Getter
public class PacienteBasico {

    private String id;
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


    public PacienteBasico(String id, PacienteExtendido pacienteExtendido) {
        this.id = id;
        this.nombre = pacienteExtendido.getNombre();
        this.genero = pacienteExtendido.getGenero();
        this.fechaNacimiento = pacienteExtendido.getFechaNacimiento();
        this.edad = pacienteExtendido.getEdad();
        this.estadoCivil = pacienteExtendido.getEstadoCivil();
        this.raza = pacienteExtendido.getRaza();
        this.lugarNacimiento = pacienteExtendido.getLugarNacimiento();
        this.alturaValor = pacienteExtendido.getAlturaValor();
        this.alturaUnidad = pacienteExtendido.getAlturaUnidad();
        this.pesoValor = pacienteExtendido.getPesoValor();
        this.pesoUnidad = pacienteExtendido.getPesoUnidad();
    }
}

