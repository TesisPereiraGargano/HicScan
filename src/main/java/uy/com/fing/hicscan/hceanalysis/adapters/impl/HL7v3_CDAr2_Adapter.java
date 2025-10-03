package uy.com.fing.hicscan.hceanalysis.adapters.impl;

import ch.qos.logback.core.joran.sanity.Pair;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import uy.com.fing.hicscan.hceanalysis.adapters.HCEAdapter;
import uy.com.fing.hicscan.hceanalysis.dto.Autor;
import uy.com.fing.hicscan.hceanalysis.dto.Paciente;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;
import org.w3c.dom.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Map;


import static uy.com.fing.hicscan.hceanalysis.utils.XmlUtils.*;

@Slf4j
public class HL7v3_CDAr2_Adapter implements HCEAdapter {
    Paciente paciente;
    Autor autor;
    String textoLibre;
    List<SustanciaAdministrada> medicamentos;

    @Override
    public void parse(File file) throws IOException {
        //Valido el XML
        String pathXML = file.getPath();
        String pathXSD = "D:/GitHub/HicScan/src/main/resources/xsd/CDA.xsd"; // ruta absoluta válida
        boolean valido = validarXML(pathXML, pathXSD);
        if (!valido) {
            log.warn("[Adaptador HL7v3CDAR2] El XML de la HCE NO valido contra el XSD ({})", pathXSD);
        }
        log.info("[Adaptador HL7v3CDAR2] Parseando archivo HL7 v3 CDA R2: " + file.getName());


        //Extraigo los valores mas importantes del CDA
        try {
            this.paciente = extraerInformacionPaciente(pathXML);
            this.autor = extraerInformacionAutor(pathXML);
            this.textoLibre = extraerTextoLibre(pathXML);
            this.medicamentos = extraerMedicamentos(pathXML);

            log.info("[Adaptador HL7v3CDAR2] Extraccion finalizada");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

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

    public static String extraerTextoLibre(String XMLpath) throws Exception {
        List<Element> elementosText = extraerInfoByXPath(XMLpath, "//text");
        StringBuilder sb = new StringBuilder();
        for (Element elem : elementosText) {
            sb.append(elem.getTextContent().trim());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public static List<SustanciaAdministrada> extraerMedicamentos(String XMLPath) throws Exception {
        //voy a las sustancias administradas
        List<SustanciaAdministrada> sustancias = new ArrayList<>();
        List<Element> entries = extraerInfoByXPath(XMLPath, "//entry/substanceAdministration");
        for (Element substAdmin : entries) {
            // Dose Quantity
            String doseQuantityValue = extraerValorDeElemento(substAdmin, "doseQuantity", "value");
            String doseQuantityUnit = extraerValorDeElemento(substAdmin, "doseQuantity", "unit");

            // Period Administration
            Element effectiveTime = (Element) substAdmin.getElementsByTagName("effectiveTime").item(0);
            String periodAdminValue = extraerValorDeElemento(effectiveTime, "period", "value");
            String periodAdminUnit = extraerValorDeElemento(effectiveTime, "period", "unit");

            // Recorre todos los posibles drugs codes bajo consumable-manufacturedProduct-manufacturedLabeledDrug-code
            List<Map.Entry<String, String>> drugsCodes = new ArrayList<>();
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
                            String codeSystemName = codeElem.getAttribute("codeSystemName");
                            if (code != null && !code.isEmpty() && codeSystemName != null && !codeSystemName.isEmpty()) {
                                drugsCodes.add(new AbstractMap.SimpleEntry<>(code, codeSystemName));
                            }
                        }
                    }
                }
            }
            sustancias.add(new SustanciaAdministrada(doseQuantityUnit, doseQuantityValue, periodAdminValue, periodAdminUnit, drugsCodes));

        }

    return sustancias;
    }


}
