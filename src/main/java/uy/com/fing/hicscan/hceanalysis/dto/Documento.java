package uy.com.fing.hicscan.hceanalysis.dto;

import lombok.Getter;

@Getter
public class Documento {
    String id; //Identificador único del documento
    String templateId; //Código OID de la plantilla de documento
    String effectiveTime; //Fecha de creación del documento
    String languageCode; //Idioma principal del documento
    String code; //Tipo de documento
    String title; //Título del documento

    public Documento(String id, String templateId, String effectiveTime, String languageCode, String code, String title){
        this.id = id;
        this.templateId = templateId;
        this.effectiveTime = effectiveTime;
        this.languageCode = languageCode;
        this.code = code;
        this.title = title;
    }

}
