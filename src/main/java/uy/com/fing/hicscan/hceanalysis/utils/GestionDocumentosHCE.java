package uy.com.fing.hicscan.hceanalysis.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Component

/**
 * Clase creada para la gestión temporal de documentos HCE (Historias Clínicas Electrónicas)
 * en memoria. Permite almacenar, recuperar y eliminar XML asociados a un identificador.
 * Se genero dado que sólo se va a trabajar con ejemplos en la POC, no se va a permitir cargar
 * HCEs a través de la aplicación.
 */
public class GestionDocumentosHCE {
    /**
     * Map concurrente que almacena documentos HCE en formato XML.
     * Clave: Identificador del documento (String)
     * Valor: Contenido XML del documento
     */
    private static final Map<String, String> documentos = new ConcurrentHashMap<>();

    public static void guardarDocumento(String id, String xml) {
        documentos.put(id, xml);
    }

    public static String obtenerDocumento(String id) {
        return documentos.get(id);
    }

    public static boolean existeDocumento(String id) {
        return documentos.containsKey(id);
    }

    public static void eliminarDocumento(String id) {
        documentos.remove(id);
    }

    /**
     * Constructor de la clase.
     * Al inicializar el componente, carga los documentos HCE de prueba
     * ubicados en el recurso {@code /HCE_CArgadas/*}.
     */
    public GestionDocumentosHCE() {
        //Carga de HCE generadas para las pruebas
        try {
            String xml = new String(getClass().getResourceAsStream("/HCE_CArgadas/JuanPerez.xml").readAllBytes(), StandardCharsets.UTF_8);
            documentos.put("1", xml);
        } catch (IOException e) {
            log.error("Error al cargar las HCE de prueba");
        }
    }
}