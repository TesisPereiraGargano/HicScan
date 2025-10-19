package uy.com.fing.hicscan.hceanalysis.adapters.impl;

import ch.qos.logback.core.joran.sanity.Pair;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapter;
import uy.com.fing.hicscan.hceanalysis.dto.*;
import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import static uy.com.fing.hicscan.hceanalysis.utils.XmlUtils.*;

/**
 * Implementación de un {@link HCEAdapter} específico para archivos HL7 CDA R2 en formato XML.
 *
 * Este adaptador realiza la validación, extracción y parseo de datos relevantes
 * de una historia clínica en formato CDA.
 *
 * Permite obtener datos como paciente, autor, medicamentos, observaciones y texto libre.
 */
@Getter
@Slf4j
public class HL7v3_CDAr2_Adapter implements HCEAdapter {
    private Paciente paciente;
    private Autor autor;
    private String textoLibre;
    private List<SustanciaAdministrada> medicamentos;
    private List<Observacion> observaciones;

    @Override
    public void parse(File file) throws IOException {
        //Valido el XML
        String pathXML = file.getPath();
        String pathXSD = "D:/GitHub/HicScan/src/main/resources/xsd/CDA.xsd"; // ruta absoluta válida
        boolean valido = validarXML(pathXML, pathXSD);
        if (!valido) {
            log.warn("[parse] El XML de la HCE NO valido contra el XSD ({})", pathXSD);
        }
        log.info("[parse] Parseando archivo HL7 v3 CDA R2: " + file.getName());


        //Extraigo los valores mas importantes del CDA
        try {
            this.paciente = extraerInformacionPaciente(pathXML);
            log.info("Finalizada -- Extraccion de información de Paciente");

            this.autor = extraerInformacionAutor(pathXML);
            log.info("Finalizada -- Extraccion de información de Autor");

            this.medicamentos = extraerMedicamentos(pathXML);
            log.info("Finalizada -- Extraccion de Medicamentos");
            log.info("Los medicamentos extraídos son: {}", this.medicamentos);

            this.observaciones = extraerObervaciones(pathXML);
            log.info("Finalizada -- Extraccion de Observaciones");

            this.textoLibre = extraerTextoLibre(pathXML);
            log.info("Finalizada -- Extraccion de información de Texto Libre");

            log.info("HCE PROCESADA CORRECTAMENTE");
        } catch (Exception e) {
            log.error("ERROR al realizar extraccion de informacion de HCE");
            throw new RuntimeException(e);
        }


    }

    /**
     * Extrae la información básica del paciente desde el archivo CDA.
     *
     * Busca en el XML los nodos correspondientes al paciente y obtiene atributos
     * como nombre, género, fecha de nacimiento, raza, lugar de nacimiento, etc.
     *
     * @param XMLpath ruta del archivo XML.
     * @return {@link Paciente} con los datos extraídos, o null si no se encuentra.
     * @throws Exception si hay errores en el procesamiento del XML.
     */
    public static Paciente extraerInformacionPaciente(String XMLpath) throws Exception {
        List<Element> patient = extraerInfoByXPath(XMLpath, "//patient");
        if (patient.isEmpty()) return null;

        Element paciente = patient.get(0);
        String nombre = extraerValorDeElemento(paciente, "name", null);
        String genero = extraerValorDeElemento(paciente, "administrativeGenderCode", "code");
        String fechaNacimiento = extraerValorDeElemento(paciente, "birthTime", "value");
        String raza = extraerValorDeElemento(paciente, "raceCode", "code");
        String lugarNacimiento = extraerValorDeElemento(paciente, "addr", null);
        String estadoCivil = extraerValorDeElemento(paciente, "maritalStatusCode", "code");

        return new Paciente(nombre, genero, fechaNacimiento, estadoCivil, raza, lugarNacimiento);

    }

    /**
     * Extrae los datos del autor de la historia clínica del CDA.
     *
     * Busca en el XML los nodos `<author>` y obtiene atributos como tiempo, nombre y datos de organización.
     *
     * @param XMLpath ruta del archivo XML.
     * @return {@link Autor} con los datos extraídos, o null si no se encuentra.
     * @throws Exception si hay errores en el procesamiento.
     */
    public static Autor extraerInformacionAutor(String XMLpath) throws Exception {
        List<Element> author = extraerInfoByXPath(XMLpath, "//author");
        if (author.isEmpty()) return null;

        Element autor = author.get(0);
        String time = extraerValorDeElemento(autor, "time", "value");
        Element assignedAuthor = (Element) autor.getElementsByTagName("assignedAuthor").item(0);
        Element assignedPerson = (Element) assignedAuthor.getElementsByTagName("assignedPerson").item(0);
        String name = extraerValorDeElemento(assignedPerson, "name", null);

        Element representedOrganization = (Element) assignedAuthor.getElementsByTagName("representedOrganization").item(0);
        String organizationName = extraerValorDeElemento(representedOrganization, "name", null);
        String organizationId = extraerValorDeElemento(assignedAuthor, "id", "root");

        return new Autor(time, name, organizationId, organizationName);
    }

    /**
     * Extrae todo el texto libre contenido en la HCE.
     *
     * Junta todos los nodos `<text>` en una sola cadena concatenada.
     *
     * @param XMLpath ruta del archivo XML.
     * @return String con el contenido textual completo.
     * @throws Exception si hay errores en el procesamiento del XML.
     */
    public static String extraerTextoLibre(String XMLpath) throws Exception {
        List<Element> elementosText = extraerInfoByXPath(XMLpath, "//text");
        StringBuilder sb = new StringBuilder();
        for (Element elem : elementosText) {
            sb.append(elem.getTextContent().trim());
            sb.append(" ");
        }
        return sb.toString().trim();
    }


    /**
     * Extrae la lista de sustancias administradas (medicamentos, terapias) del CDA.
     *
     * @param XMLPath ruta del archivo XML.
     * @return Lista de {@link SustanciaAdministrada}.
     * @throws Exception si hay errores en el parseo del XML.
     */
    public static List<SustanciaAdministrada> extraerMedicamentos(String XMLPath) throws Exception {
        //voy a las sustancias administradas
        List<SustanciaAdministrada> sustancias = new ArrayList<>();
        List<Element> entries = extraerInfoByXPath(XMLPath, "//entry/substanceAdministration");
        for (Element substAdmin : entries) {
            // Name
            String name = extraerValorDeElemento(substAdmin, "text", null);
            // Dose Quantity
            String doseQuantityValue = extraerValorDeElemento(substAdmin, "doseQuantity", "value");
            String doseQuantityUnit = extraerValorDeElemento(substAdmin, "doseQuantity", "unit");

            // Period Administration
            Element effectiveTime = (Element) substAdmin.getElementsByTagName("effectiveTime").item(0);
            String periodAdminValue = extraerValorDeElemento(effectiveTime, "period", "value");
            String periodAdminUnit = extraerValorDeElemento(effectiveTime, "period", "unit");

            // Recorre todos los posibles drugs codes bajo consumable-manufacturedProduct-manufacturedLabeledDrug-code
            List<Droga> drugs = new ArrayList<>();
            NodeList consumables = substAdmin.getElementsByTagName("consumable");
            for (int i = 0; i < consumables.getLength(); i++) {
                Element consumable = (Element) consumables.item(i);
                NodeList manufacturedProducts = consumable.getElementsByTagName("manufacturedProduct");
                for (int j = 0; j < manufacturedProducts.getLength(); j++) {
                    Element product = (Element) manufacturedProducts.item(j);
                    NodeList labeledDrugs = product.getElementsByTagName("manufacturedLabeledDrug");
                    for (int k = 0; k < labeledDrugs.getLength(); k++) {
                        Element labeledDrug = (Element) labeledDrugs.item(k);
                        NodeList codes = labeledDrug.getElementsByTagName("code");
                        for (int l = 0; l < codes.getLength(); l++) {
                            Element codeElem = (Element) codes.item(l);
                            String code = codeElem.getAttribute("code");
                            String nameDrug = codeElem.getAttribute("displayName");
                            String codeSystem = codeElem.getAttribute("codeSystem");
                            if (code != null && !code.isEmpty() && codeSystem != null && !codeSystem.isEmpty()) {
                                CodDiccionario cods = new CodDiccionario();
                                switch (codeSystem) {
                                    case "2.16.840.1.113883.6.96":
                                        cods.setSnomedCT(code);
                                        break;
                                    case "2.16.840.1.113883.6.88":
                                        cods.setRxnorm(code);
                                        break;
                                    case "2.16.840.1.113883.6.86":
                                        cods.setCui(code);
                                        break;
                                        //case "2.16.840.1.113883.6.1":
                                    //     "LOINC";
                                    //case "2.16.840.1.113883.6.90":
                                    //     "CPT";
                                    //case "2.16.840.1.113883.6.3":
                                    //     "ICD-10";
                                    // ...
                                    default:
                                        log.error("[extraerMedicamentos] El codigo no pertenence a los diccionarios definidos");
                                        break;
                                }
                                Droga droga = new Droga(cods, nameDrug);
                                drugs.add(droga);
                            }
                        }
                    }
                }
            }
            sustancias.add(new SustanciaAdministrada(name, doseQuantityUnit, doseQuantityValue, periodAdminValue, periodAdminUnit, drugs));

        }

    return sustancias;
    }


    /**
     * Extrae todas las observaciones clínicas del CDA.
     *
     * Ordena los nodos `<observation>` y crea objetos de tipo {@link Observacion}
     * con código, sistema, estado, tiempo, valor y unidad.
     *
     * @param XMLPath ruta del archivo XML.
     * @return Lista de {@link Observacion}.
     * @throws Exception si hay errores en la extracción.
     */
    public static List<Observacion> extraerObervaciones(String XMLPath) throws Exception {
        List<Observacion> observaciones = new ArrayList<>();
        List<Element> entries = extraerInfoByXPath(XMLPath, "//entry/observation");

         for (Element observacion : entries){
             String codeSystem = extraerValorDeElemento(observacion, "code", "codeSystem");
             String code = extraerValorDeElemento(observacion, "code", "code");
             String statusCode = extraerValorDeElemento(observacion, "statusCode", "code");
             String effectiveTime = extraerValorDeElemento(observacion, "effectiveTime", "value");
             String medicionValue = extraerValorDeElemento(observacion, "value", "value");
             String medicionUnit = extraerValorDeElemento(observacion, "value", "unit");

            Observacion obs = new Observacion(code, codeSystem, statusCode, effectiveTime, medicionValue, medicionUnit);
            observaciones.add(obs);
         }

        return observaciones;
    }

}
