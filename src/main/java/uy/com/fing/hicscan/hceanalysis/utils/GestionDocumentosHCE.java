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
public class GestionDocumentosHCE {

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