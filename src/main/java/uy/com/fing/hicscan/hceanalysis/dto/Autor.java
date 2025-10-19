package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;
/**
 * Representa la información del autor que participó en la generación o
 * contribución de una Historia Clínica Electrónica (HCE).
 *
 * Este DTO encapsula los datos básicos del profesional de la salud asociado al
 * documento clínico, incluyendo su nombre, identificador de organización y
 * momento de participación en el registro.
 *
 */
@Getter
public class Autor {
    String time; //Tiempo en que el autor comienza su participación en el documento
    String name; //Nombre del trabajador de la salud
    String organizationId; //Identificador de la organización
    String organizationName; //Nombre de la organización

    public Autor(String time, String name, String organizationId, String organizationName){
        this.time = time;
        this.name = name;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
    }
}
