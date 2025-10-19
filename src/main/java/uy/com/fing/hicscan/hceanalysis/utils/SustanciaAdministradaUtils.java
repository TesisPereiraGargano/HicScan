package uy.com.fing.hicscan.hceanalysis.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import uy.com.fing.hicscan.hceanalysis.dto.CodDiccionario;
import uy.com.fing.hicscan.hceanalysis.dto.Droga;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;
import org.springframework.web.client.HttpClientErrorException;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SustanciaAdministradaUtils {
    //Para poder hacer la clasificación
    public enum ClasificacionDiureticoResultado {
        ES_DIURETICO, NO_ES_DIURETICO, FALTA_RXNORM
    }


    /**
     * Procesa una lista de objetos SustanciaAdministrada para enriquecerlos con códigos RXNORM faltantes.
     * Evita duplicados basándose en el código CUI (Concept Unique Identifier).
     *
     * @param sustancias Lista de SustanciaAdministrada a procesar.
     * @param apiKeyUMLS Clave de API para acceder a los servicios UMLS.
     * @return Nueva lista de SustanciaAdministrada sin duplicados y con códigos RXNORM completados.
     */
    public static List<SustanciaAdministrada> procesarYEnriquecerMedicamentos(List<SustanciaAdministrada> sustancias, String apiKeyUMLS) {
        Set<String> cuiSet = new HashSet<>();
        List<SustanciaAdministrada> resultado = new ArrayList<>();

        for (SustanciaAdministrada sust : sustancias) {
            for (int i = 0; i < sust.getDrugs().size(); i++) {
                String cui = sust.getDrugs().get(i).getCodigos().getCui();
                if (cui != null && !cuiSet.contains(cui)) {
                    cuiSet.add(cui);

                    // Si no tiene código RXNORM, lo obtengo
                    if (sust.getDrugs().get(i).getCodigos().getRxnorm().isBlank()) {
                        String rxnorm = obtenerRxNormDesdeCUI(cui, apiKeyUMLS);
                        sust.getDrugs().get(i).getCodigos().setRxnorm(rxnorm);
                    }
                    resultado.add(sust);
                }
            }

        }
        return resultado;
    }
    /**
     * Consulta el servicio UMLS para obtener el código RXNORM asociado a un CUI dado.
     * Hace una llamada REST al endpoint de átomos de UMLS filtrando la fuente RXNORM.
     * Procesa la respuesta JSON para extraer únicamente el código numérico RXNORM.
     *
     * @param cui Código UMLS CUI del concepto.
     * @param apiKeyUMLS Clave de API para acceder a los servicios UMLS.
     * @return Código RXNORM correspondiente al CUI, o cadena vacía si no se encuentra.
     */
    public static String obtenerRxNormDesdeCUI(String cui, String apiKeyUMLS) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String url = "https://uts-ws.nlm.nih.gov/rest/content/current/CUI/" + cui + "/atoms?sabs=RXNORM&apiKey=" + apiKeyUMLS;
        String rxnorm = "";
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode res = mapper.readTree(response);
            for (JsonNode atom : res.path("result")) {
                if ("RXNORM".equals(atom.path("rootSource").asText())) {
                    String rxnormCodeUrl = atom.path("code").asText();
                    Pattern pattern = Pattern.compile(".*/RXNORM/(\\d+)$");
                    Matcher matcher = pattern.matcher(rxnormCodeUrl);
                    if (matcher.find()) {
                        rxnorm = matcher.group(1);
                        break; //Devuelvo sólo el número del RXNORM code y no la URL completa
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return "";
            }
            throw e; // Otros errores sí relanzar
        } catch (Exception e) {
            throw new RuntimeException("Error procesando la consulta a UMLS", e);
        }
        return rxnorm;
    }
    /**
     * Obtiene el CUI (Concept Unique Identifier) a partir de un código SNOMED CT consultando UMLS.
     * Realiza una llamada REST al endpoint de búsqueda UMLS filtrando por SNOMEDCT.
     * Procesa el JSON para extraer el primer CUI encontrado en los resultados.
     *
     * @param code_snomedct Código SNOMED CT del concepto.
     * @param apiKeyUMLS Clave de API para acceder a los servicios UMLS.
     * @return CUI asociado al código SNOMED CT o cadena vacía si no se encuentra.
     */
    public static String obtenerCUIDesdeSNOMEDCT(String code_snomedct, String apiKeyUMLS) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String url = "https://uts-ws.nlm.nih.gov/rest/search/current?string=" + code_snomedct + "&sab=SNOMEDCT&apiKey=" + apiKeyUMLS;
        String response = restTemplate.getForObject(url, String.class);
        String ui = "";
        try {
            JsonNode res = mapper.readTree(response);
            JsonNode resultNode = res.path("result");
            JsonNode resultsArray = resultNode.path("results");

            if (resultsArray.isArray() && resultsArray.size() > 0) {
                JsonNode firstResult = resultsArray.get(0);
                ui = firstResult.path("ui").asText(null);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error procesando JSON de UMLS", e);
        }
        return ui;
    }
    /**
     * Completa los códigos RXNORM en la lista de medicamentos proporcionada, usando la clave UMLS API.
     * Si un medicamento no tiene código CUI, lo obtiene usando el código SNOMED CT asociado.
     * Luego, con el CUI, obtiene el código RXNORM y lo asigna al medicamento.
     * Registra error si el medicamento no tiene ninguno de los códigos básicos (CUI o SNOMED CT).
     *
     * @param meds Lista de SustanciaAdministrada para procesar y enriquecer.
     * @param umlsApiKey Clave API para llamadas a servicios UMLS.
     */
    public static void poblarCodigosRxNorm(List<SustanciaAdministrada> meds, String umlsApiKey) {
        for (SustanciaAdministrada sust : meds) {
            //Completo los CUIS (si corresponde) y hallo los RXNORM
            for (Droga droga : sust.getDrugs()) {
                CodDiccionario codigos = droga.getCodigos();
                if (codigos.getRxnorm().isBlank()) {
                    if (codigos.getCui().isBlank() && codigos.getSnomedCT().isBlank()) {
                        log.error("Error - el medicamento no puede tener todos los códigos vacíos");
                    } else if (codigos.getCui().isBlank()) {
                        codigos.setCui(obtenerCUIDesdeSNOMEDCT(codigos.getSnomedCT(), umlsApiKey));
                    }
                    if (!codigos.getCui().isBlank()) {
                        codigos.setRxnorm(obtenerRxNormDesdeCUI(codigos.getCui(), umlsApiKey));
                    }
                    droga.setCodigos(codigos);
                }

            }
        }

    }

    /**
     * Dice si un medicamento es diurético usando el código ATC obtenido a partir de un código RxNorm.
     * @param medicamento es una sustancia que puede contener una droga con código RxNorm
     * @return booleano que dice si es diuretico o no (o null si no lo pudo clasificar)
     */
    public static Boolean esDiuretico(SustanciaAdministrada medicamento) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        boolean hay_diuretico = false;
        boolean hay_sust_sinRXNORM = false;

        for (Droga droga : medicamento.getDrugs()) {
            String rxnorm = droga.getCodigos().getRxnorm();
            if (rxnorm == null || rxnorm.isBlank()) {
                hay_sust_sinRXNORM = true;
                continue;
            }

            try {
                String url = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byRxcui.json"
                        + "?rxcui=" + rxnorm
                        + "&relaSource=ATC";

                log.info("[esDiuretico] Consultando RxNav: {}", url);

                String response = restTemplate.getForObject(url, String.class);
                JsonNode root = mapper.readTree(response);
                JsonNode listNode = root.path("rxclassDrugInfoList").path("rxclassDrugInfo");

                if (listNode.isArray() && !listNode.isEmpty()) {
                    for (JsonNode node : listNode) {
                        JsonNode rxclassItem = node.path("rxclassMinConceptItem");
                        String className = rxclassItem.path("className").asText("").toLowerCase();
                        String classId = rxclassItem.path("classId").asText("");

                        if (classId.startsWith("C03") || className.contains("diuretic")) {
                            log.info("[esDiuretico] {} pertenece al grupo ATC de diuréticos o tiene nombre que contiene 'diuretic' ({}).", rxnorm, classId);
                            hay_diuretico = true;
                            break;
                        }
                    }
                } else {
                    //Response que no tiene código ATC por lo que no está clasificado
                    hay_sust_sinRXNORM = true;
                }

            } catch (HttpClientErrorException e) {
                log.warn("[esDiuretico] RxCUI {} inválido o rechazado: {}", rxnorm, e.getStatusCode());
            } catch (Exception e) {
                log.error("[esDiuretico] Error procesando RxCUI {}", rxnorm, e);
            }
        }

        // Resultado final según las banderas
        if (hay_diuretico) return true; //No importa si hay uno sin RXNORM porque de los que tienen uno es diretico
        if (hay_sust_sinRXNORM) return null; //Los que se pudieron consultar no están clasificados como diureticos pero hay al menos 1 que no se puede clasficar
        return false;
    }

}





