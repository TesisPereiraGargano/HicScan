package uy.com.fing.hicscan.hceanalysis.utils;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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
        documentos.put("1", "<?xml version=\"1.0\"?>\n" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"CDA.xsl\"?>\n" +
                "<ClinicalDocument xmlns=\"urn:hl7-org:v3\" xmlns:voc=\"urn:hl7-org:v3/voc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:hl7-org:v3 CDA.xsd\">\n" +
                "\t<!-- \n" +
                "********************************************************\n" +
                "  CDA Header\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t<typeId root=\"2.16.840.1.113883.1.3\" extension=\"POCD_HD000040\"/>\n" +
                "\t<templateId root=\"2.16.840.1.113883.3.27.1776\"/>\n" +
                "\t<id extension=\"c266\" root=\"2.16.840.1.113883.19.4\"/>\n" +
                "\t<code code=\"11488-4\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Consultation note\"/>\n" +
                "\t<title>Good Health Clinic Consultation Note</title>\n" +
                "\t<effectiveTime value=\"20000407\"/>\n" +
                "\t<confidentialityCode code=\"N\" codeSystem=\"2.16.840.1.113883.5.25\"/>\n" +
                "\t<languageCode code=\"en-US\"/>\n" +
                "\t<setId extension=\"BB35\" root=\"2.16.840.1.113883.19.7\"/>\n" +
                "\t<versionNumber value=\"2\"/>\n" +
                "\t<recordTarget>\n" +
                "\t\t<patientRole>\n" +
                "\t\t\t<id extension=\"12345\" root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t<patient>\n" +
                "\t\t\t\t<name>\n" +
                "\t\t\t\t\t<given>Juan</given>\n" +
                "\t\t\t\t\t<family>Perez</family>\n" +
                "\t\t\t\t</name>\n" +
                "\t\t\t\t<administrativeGenderCode code=\"M\" codeSystem=\"2.16.840.1.113883.5.1\"/>\n" +
                "\t\t\t\t<birthTime value=\"19900924\"/>\n" +
                "\t\t\t</patient>\n" +
                "\t\t\t<providerOrganization>\n" +
                "\t\t\t\t<id root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t</providerOrganization>\n" +
                "\t\t</patientRole>\n" +
                "\t</recordTarget>\n" +
                "\t<author>\n" +
                "\t\t<time value=\"2025040714\"/>\n" +
                "\t\t<assignedAuthor>\n" +
                "\t\t\t<id extension=\"KP00017\" root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t<assignedPerson>\n" +
                "\t\t\t\t<name>\n" +
                "\t\t\t\t\t<given>Roberto</given>\n" +
                "\t\t\t\t\t<family>Pereira</family>\n" +
                "\t\t\t\t\t<suffix>MD</suffix>\n" +
                "\t\t\t\t</name>\n" +
                "\t\t\t</assignedPerson>\n" +
                "\t\t\t<representedOrganization>\n" +
                "\t\t\t\t<id root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t</representedOrganization>\n" +
                "\t\t</assignedAuthor>\n" +
                "\t</author>\n" +
                "\t<custodian>\n" +
                "\t\t<assignedCustodian>\n" +
                "\t\t\t<representedCustodianOrganization>\n" +
                "\t\t\t\t<id root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t\t<name>Hospital de Clínicas</name>\n" +
                "\t\t\t</representedCustodianOrganization>\n" +
                "\t\t</assignedCustodian>\n" +
                "\t</custodian>\n" +
                "\t<legalAuthenticator>\n" +
                "\t\t<time value=\"20250408\"/>\n" +
                "\t\t<signatureCode code=\"S\"/>\n" +
                "\t\t<assignedEntity>\n" +
                "\t\t\t<id extension=\"KP00017\" root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t<assignedPerson>\n" +
                "\t\t\t\t<name>\n" +
                "\t\t\t\t\t<given>Roberto</given>\n" +
                "\t\t\t\t\t<family>Pereira</family>\n" +
                "\t\t\t\t\t<suffix>MD</suffix>\n" +
                "\t\t\t\t</name>\n" +
                "\t\t\t</assignedPerson>\n" +
                "\t\t\t<representedOrganization>\n" +
                "\t\t\t\t<id root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t</representedOrganization>\n" +
                "\t\t</assignedEntity>\n" +
                "\t</legalAuthenticator>\n" +
                "\t<relatedDocument typeCode=\"RPLC\">\n" +
                "\t\t<parentDocument>\n" +
                "\t\t\t<id extension=\"a123\" root=\"2.16.840.1.113883.19.4\"/>\n" +
                "\t\t\t<setId extension=\"BB35\" root=\"2.16.840.1.113883.19.7\"/>\n" +
                "\t\t\t<versionNumber value=\"1\"/>\n" +
                "\t\t</parentDocument>\n" +
                "\t</relatedDocument>\n" +
                "\t<componentOf>\n" +
                "\t\t<encompassingEncounter>\n" +
                "\t\t\t<id extension=\"KPENC1332\" root=\"2.16.840.1.113883.19.6\"/>\n" +
                "\t\t\t<effectiveTime value=\"20000407\"/>\n" +
                "\t\t\t<encounterParticipant typeCode=\"CON\">\n" +
                "\t\t\t\t<time value=\"20000407\"/>\n" +
                "\t\t\t\t<assignedEntity>\n" +
                "\t\t\t\t\t<id extension=\"KP00017\" root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t\t\t<assignedPerson>\n" +
                "\t\t\t\t\t\t<name>\n" +
                "\t\t\t\t\t\t\t<given>Roberto</given>\n" +
                "\t\t\t\t\t\t\t<family>Pereira</family>\n" +
                "\t\t\t\t\t\t\t<suffix>MD</suffix>\n" +
                "\t\t\t\t\t\t</name>\n" +
                "\t\t\t\t\t</assignedPerson>\n" +
                "\t\t\t\t\t<representedOrganization>\n" +
                "\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.5\"/>\n" +
                "\t\t\t\t\t</representedOrganization>\n" +
                "\t\t\t\t</assignedEntity>\n" +
                "\t\t\t</encounterParticipant>\n" +
                "\t\t\t<location>\n" +
                "\t\t\t\t<healthCareFacility classCode=\"DSDLOC\">\n" +
                "\t\t\t\t\t<code code=\"GIM\" codeSystem=\"2.16.840.1.113883.5.10588\" displayName=\"General internal medicine clinic\"/>\n" +
                "\t\t\t\t</healthCareFacility>\n" +
                "\t\t\t</location>\n" +
                "\t\t</encompassingEncounter>\n" +
                "\t</componentOf>\n" +
                "\t<!-- \n" +
                "********************************************************\n" +
                "  CDA Body\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t<component>\n" +
                "\t\t<structuredBody>\n" +
                "\t\t\t<!-- \n" +
                "********************************************************\n" +
                "  Seccion de la afeccion que presenta en este momento\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t\t\t<component>\n" +
                "\t\t\t\t<section>\n" +
                "\t\t\t\t\t<code code=\"10164-2\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t<title>Historia de la enfermedad actual</title>\n" +
                "\t\t\t\t\t<text>\n" +
                "\t\t\t\t\t\t<content styleCode=\"Bold\">Juan Perez\n" +
                "\t\t\t\t\t\t</content> \n" +
                "\t\t\t\t\t\tEs un hombre de 35 años remitido para tratamiento adicional del asma. El asma comenzó en su adolescencia.\n" +
                "\t\t\t\t\t\tFue hospitalizado dos veces el año pasado y dos veces este año. No ha podido suspender gradualmente los esteroides en los últimos meses.\n" +
                "\t\t\t\t\t</text>\n" +
                "\t\t\t\t</section>\n" +
                "\t\t\t</component>\n" +
                "\t\t\t<!-- \n" +
                "********************************************************\n" +
                "  Sección de historial médico de su pasado\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t\t\t<component>\n" +
                "\t\t\t\t<section>\n" +
                "\t\t\t\t\t<code code=\"10153-2\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t<title>Historial médico pasado</title>\n" +
                "\t\t\t\t\t<text>\n" +
                "\t\t\t\t\t\t<list>\n" +
                "\t\t\t\t\t\t\t<item>\n" +
                "\t\t\t\t\t\t\t\t<content ID=\"a1\">Asma</content>\n" +
                "\t\t\t\t\t\t\t</item>\n" +
                "\t\t\t\t\t\t\t<item>\n" +
                "\t\t\t\t\t\t\t\t<content ID=\"a2\">Hipertensiom</content>\n" +
                "\t\t\t\t\t\t\t</item>\n" +
                "\t\t\t\t\t\t</list>\n" +
                "\t\t\t\t\t</text>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"COND\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"195967001\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Asthma\">\n" +
                "\t\t\t\t\t\t\t\t<originalText>\n" +
                "\t\t\t\t\t\t\t\t\t<reference value=\"#a1\"/>\n" +
                "\t\t\t\t\t\t\t\t</originalText>\n" +
                "\t\t\t\t\t\t\t</code>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<effectiveTime value=\"1950\"/>\n" +
                "\t\t\t\t\t\t\t<reference typeCode=\"XCRPT\">\n" +
                "\t\t\t\t\t\t\t\t<externalObservation>\n" +
                "\t\t\t\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.1.2765\"/>\n" +
                "\t\t\t\t\t\t\t\t</externalObservation>\n" +
                "\t\t\t\t\t\t\t</reference>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"COND\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"59621000\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"HTN\">\n" +
                "\t\t\t\t\t\t\t\t<originalText>\n" +
                "\t\t\t\t\t\t\t\t\t<reference value=\"#a2\"/>\n" +
                "\t\t\t\t\t\t\t\t</originalText>\n" +
                "\t\t\t\t\t\t\t</code>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<reference typeCode=\"SPRT\">\n" +
                "\t\t\t\t\t\t\t\t<seperatableInd value=\"false\"/>\n" +
                "\t\t\t\t\t\t\t\t<externalDocument>\n" +
                "\t\t\t\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.4.789\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<text mediaType=\"multipart/related\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<reference value=\"HTN.cda\"/>\n" +
                "\t\t\t\t\t\t\t\t\t</text>\n" +
                "\t\t\t\t\t\t\t\t\t<setId root=\"2.16.840.1.113883.19.7.2465\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<versionNumber value=\"1\"/>\n" +
                "\t\t\t\t\t\t\t\t</externalDocument>\n" +
                "\t\t\t\t\t\t\t</reference>\n" +
                "\t\t\t\t\t\t\t<reference typeCode=\"XCRPT\">\n" +
                "\t\t\t\t\t\t\t\t<externalObservation>\n" +
                "\t\t\t\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.1.2005\"/>\n" +
                "\t\t\t\t\t\t\t\t</externalObservation>\n" +
                "\t\t\t\t\t\t\t</reference>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"COND\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code xsi:type=\"CD\" code=\"396275006\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Osteoarthritis\">\n" +
                "\t\t\t\t\t\t\t\t<originalText>\n" +
                "\t\t\t\t\t\t\t\t\t<reference value=\"#a3\"/>\n" +
                "\t\t\t\t\t\t\t\t</originalText>\n" +
                "\t\t\t\t\t\t\t</code>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<targetSiteCode code=\"49076000\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Knee joint\">\n" +
                "\t\t\t\t\t\t\t\t<originalText>\n" +
                "\t\t\t\t\t\t\t\t\t<reference value=\"#a4\"/>\n" +
                "\t\t\t\t\t\t\t\t</originalText>\n" +
                "\t\t\t\t\t\t\t\t<qualifier>\n" +
                "\t\t\t\t\t\t\t\t\t<name code=\"78615007\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"with laterality\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<value code=\"24028007\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"right\"/>\n" +
                "\t\t\t\t\t\t\t\t</qualifier>\n" +
                "\t\t\t\t\t\t\t</targetSiteCode>\n" +
                "\t\t\t\t\t\t\t<reference typeCode=\"XCRPT\">\n" +
                "\t\t\t\t\t\t\t\t<externalObservation>\n" +
                "\t\t\t\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.1.1805\"/>\n" +
                "\t\t\t\t\t\t\t\t</externalObservation>\n" +
                "\t\t\t\t\t\t\t</reference>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t</section>\n" +
                "\t\t\t</component>\n" +
                "\t\t\t<!-- \n" +
                "********************************************************\n" +
                "  Medicamentos\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t\t\t<component>\n" +
                "\t\t\t\t<section>\n" +
                "\t\t\t\t\t<code code=\"10160-0\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t<title>Medicamentos</title>\n" +
                "\t\t\t\t\t<text>\n" +
                "\t\t\t\t\t\t<list>\n" +
                "\t\t\t\t\t\t\t<item>Theodur 200 mg dos veces al día</item>\n" +
                "\t\t\t\t\t\t\t<item>Inhalador Proventil 2 inhalaciones QID PRN</item>\n" +
                "\t\t\t\t\t\t\t<item>Prednisona 20mg una vez al día</item>\n" +
                "\t\t\t\t\t\t\t<item>HCTZ 25mg una vez al día</item>\n" +
                "\t\t\t\t\t\t</list>\n" +
                "\t\t\t\t\t</text>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<text>Theodur 200 mg dos veces al día</text>\n" +
                "\t\t\t\t\t\t\t<effectiveTime xsi:type=\"PIVL_TS\" institutionSpecified=\"true\">\n" +
                "\t\t\t\t\t\t\t\t<period value=\"12\" unit=\"h\"/>\n" +
                "\t\t\t\t\t\t\t</effectiveTime>\n" +
                "\t\t\t\t\t\t\t<routeCode code=\"PO\" codeSystem=\"2.16.840.1.113883.5.112\" codeSystemName=\"RouteOfAdministration\"/>\n" +
                "\t\t\t\t\t\t\t<doseQuantity value=\"200\" unit=\"mg\"/>\n" +
                "\t\t\t\t\t\t\t<consumable>\n" +
                "\t\t\t\t\t\t\t\t<manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t\t\t<manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t\t\t<code code=\"66493003\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Theophylline\"/>\n" +
                "\t\t\t\t\t\t\t\t\t</manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t</manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t</consumable>\n" +
                "\t\t\t\t\t\t</substanceAdministration>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<text>Inhalador Proventil 2 inhalaciones QID PRN</text>\n" +
                "\t\t\t\t\t\t\t<effectiveTime xsi:type=\"PIVL_TS\" institutionSpecified=\"true\">\n" +
                "\t\t\t\t\t\t\t\t<period value=\"6\" unit=\"h\"/>\n" +
                "\t\t\t\t\t\t\t</effectiveTime>\n" +
                "\t\t\t\t\t\t\t<priorityCode code=\"PRN\"/>\n" +
                "\t\t\t\t\t\t\t<routeCode code=\"IPINHL\" codeSystem=\"2.16.840.1.113883.5.112\" codeSystemName=\"RouteOfAdministration\" displayName=\"Inhalation, oral\"/>\n" +
                "\t\t\t\t\t\t\t<doseQuantity value=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<consumable>\n" +
                "\t\t\t\t\t\t\t\t<manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t\t\t<manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t\t\t<code code=\"91143003\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Albuterol\"/>\n" +
                "\t\t\t\t\t\t\t\t\t</manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t</manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t</consumable>\n" +
                "\t\t\t\t\t\t</substanceAdministration>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.8.1\"/>\n" +
                "\t\t\t\t\t\t\t<text>Prednisona 20mg una vez al día</text>\n" +
                "\t\t\t\t\t\t\t<effectiveTime xsi:type=\"PIVL_TS\" institutionSpecified=\"true\">\n" +
                "\t\t\t\t\t\t\t\t<period value=\"24\" unit=\"h\"/>\n" +
                "\t\t\t\t\t\t\t</effectiveTime>\n" +
                "\t\t\t\t\t\t\t<routeCode code=\"PO\" codeSystem=\"2.16.840.1.113883.5.112\" codeSystemName=\"RouteOfAdministration\"/>\n" +
                "\t\t\t\t\t\t\t<doseQuantity value=\"20\" unit=\"mg\"/>\n" +
                "\t\t\t\t\t\t\t<consumable>\n" +
                "\t\t\t\t\t\t\t\t<manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t\t\t<manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t\t\t<code code=\"10312003\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Prednisone preparation\"/>\n" +
                "\t\t\t\t\t\t\t\t\t</manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t</manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t</consumable>\n" +
                "\t\t\t\t\t\t</substanceAdministration>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<text>HCTZ 25mg una vez al día</text>\n" +
                "\t\t\t\t\t\t\t<effectiveTime xsi:type=\"PIVL_TS\" institutionSpecified=\"true\">\n" +
                "\t\t\t\t\t\t\t\t<period value=\"24\" unit=\"h\"/>\n" +
                "\t\t\t\t\t\t\t</effectiveTime>\n" +
                "\t\t\t\t\t\t\t<routeCode code=\"PO\" codeSystem=\"2.16.840.1.113883.5.112\" codeSystemName=\"RouteOfAdministration\"/>\n" +
                "\t\t\t\t\t\t\t<consumable>\n" +
                "\t\t\t\t\t\t\t\t<manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t\t\t<manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t\t\t<code code=\"376209006\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Hydrochlorothiazide 25mg tablet\"/>\n" +
                "\t\t\t\t\t\t\t\t\t</manufacturedLabeledDrug>\n" +
                "\t\t\t\t\t\t\t\t</manufacturedProduct>\n" +
                "\t\t\t\t\t\t\t</consumable>\n" +
                "\t\t\t\t\t\t</substanceAdministration>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t</section>\n" +
                "\t\t\t</component>\n" +
                "\t\t\t<!-- \n" +
                "********************************************************\n" +
                "  Alergias y reacciones adversas\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t\t\t<component>\n" +
                "\t\t\t\t<section>\n" +
                "\t\t\t\t\t<code code=\"10155-0\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t<title>Alergias y reacciones adversas</title>\n" +
                "\t\t\t\t\t<text>\n" +
                "\t\t\t\t\t\t<list>\n" +
                "\t\t\t\t\t\t\t<item>Penicilina - Urticaria</item>\n" +
                "\t\t\t\t\t\t\t<item>Aspirina - Sibilancias</item>\n" +
                "\t\t\t\t\t\t\t<item>Codeína - Picazón y náuseas</item>\n" +
                "\t\t\t\t\t\t</list>\n" +
                "\t\t\t\t\t</text>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code xsi:type=\"CD\" code=\"247472004\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Urticaria\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<entryRelationship typeCode=\"MFST\">\n" +
                "\t\t\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t\t\t<code xsi:type=\"CD\" code=\"91936005\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Alergia a la penicilina\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t\t\t</entryRelationship>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"56018004\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Sibilancias\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<entryRelationship typeCode=\"MFST\">\n" +
                "\t\t\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t\t\t<code code=\"293586001\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Alergia a la aspirina\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t\t\t</entryRelationship>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"32738000\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Prurito\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<entryRelationship typeCode=\"MFST\">\n" +
                "\t\t\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.1.2010\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<code code=\"62014003\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Reaccion adversa a la droga\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<qualifier>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<name code=\"246075003\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Agente que la causa\"/>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<value code=\"1476002\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Codeina\"/>\n" +
                "\t\t\t\t\t\t\t\t\t\t</qualifier>\n" +
                "\t\t\t\t\t\t\t\t\t</code>\n" +
                "\t\t\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t\t\t</entryRelationship>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"73879007\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Nausea\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<entryRelationship typeCode=\"MFST\">\n" +
                "\t\t\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t\t\t<id root=\"2.16.840.1.113883.19.1.2010\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<code code=\"84100007\" codeSystem=\"2.16.840.1.113883.6.96\"/>\n" +
                "\t\t\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t\t\t</entryRelationship>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t</section>\n" +
                "\t\t\t</component>\n" +
                "\t\t\t<!-- \n" +
                "********************************************************\n" +
                "  Antecedentes familiares\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t\t\t<component>\n" +
                "\t\t\t\t<section>\n" +
                "\t\t\t\t\t<code code=\"10157-2\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t<title>Historia Familiar</title>\n" +
                "\t\t\t\t\t<text>\n" +
                "\t\t\t\t\t\t<list>\n" +
                "\t\t\t\t\t\t\t<item>El padre sufrió un infarto de miocardio fatal cuando tenía unos 50 años.</item>\n" +
                "\t\t\t\t\t\t\t<item>No cancer ni diabetes.</item>\n" +
                "\t\t\t\t\t\t</list>\n" +
                "\t\t\t\t\t</text>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"22298006\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"MI\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<effectiveTime value=\"1970\"/>\n" +
                "\t\t\t\t\t\t\t<subject>\n" +
                "\t\t\t\t\t\t\t\t<relatedSubject classCode=\"PRS\">\n" +
                "\t\t\t\t\t\t\t\t\t<code code=\"FTH\" codeSystem=\"2.16.840.1.113883.5.111\"/>\n" +
                "\t\t\t\t\t\t\t\t</relatedSubject>\n" +
                "\t\t\t\t\t\t\t</subject>\n" +
                "\t\t\t\t\t\t\t<entryRelationship typeCode=\"CAUS\">\n" +
                "\t\t\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t\t\t<code code=\"399347008\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"muerte\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<effectiveTime value=\"1970\"/>\n" +
                "\t\t\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t\t\t</entryRelationship>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\" negationInd=\"true\">\n" +
                "\t\t\t\t\t\t\t<code code=\"275937001\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"No historial familiar de cancer\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<effectiveTime>\n" +
                "\t\t\t\t\t\t\t\t<high value=\"20000407\" inclusive=\"true\"/>\n" +
                "\t\t\t\t\t\t\t</effectiveTime>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"160274005\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"No hictorial familiar de diabetes\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<effectiveTime>\n" +
                "\t\t\t\t\t\t\t\t<high value=\"20000407\" inclusive=\"true\"/>\n" +
                "\t\t\t\t\t\t\t</effectiveTime>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t</section>\n" +
                "\t\t\t</component>\n" +
                "\t\t\t<!-- \n" +
                "********************************************************\n" +
                "  Historia Social\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t\t\t<component>\n" +
                "\t\t\t\t<section>\n" +
                "\t\t\t\t\t<code code=\"29762-2\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t<title>Historia social</title>\n" +
                "\t\t\t\t\t<text>\n" +
                "\t\t\t\t\t\t<list>\n" +
                "\t\t\t\t\t\t\t<item>Fumador: 1 PPD entre los 20 y los 25 años, y luego lo dejó.</item>\n" +
                "\t\t\t\t\t\t\t<item>Alcohol :: raras veces</item>\n" +
                "\t\t\t\t\t\t</list>\n" +
                "\t\t\t\t\t</text>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"266924008\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"ex fumador empedernido (20-39 cigarrillos al día)\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<effectiveTime>\n" +
                "\t\t\t\t\t\t\t\t<low value=\"2010\"/>\n" +
                "\t\t\t\t\t\t\t\t<high value=\"2015\"/>\n" +
                "\t\t\t\t\t\t\t</effectiveTime>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"160625004\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Fecha en la que dejo de fumar\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t<value xsi:type=\"TS\" value=\"2025\"/>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t<code code=\"266917007\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Bebedor trivial - menos de 1 / día\"/>\n" +
                "\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t</entry>\n" +
                "\t\t\t\t</section>\n" +
                "\t\t\t</component>\n" +
                "\t\t\t<!-- \n" +
                "********************************************************\n" +
                "  Examen fisico\n" +
                "********************************************************\n" +
                "-->\n" +
                "\t\t\t<component>\n" +
                "\t\t\t\t<section>\n" +
                "\t\t\t\t\t<code code=\"11384-5\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t<title>Examen Fisico</title>\n" +
                "\t\t\t\t\t<component>\n" +
                "\t\t\t\t\t\t<section>\n" +
                "\t\t\t\t\t\t\t<code code=\"8716-3\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\n" +
                "\t\t\t\t\t\t\t<title>Vital Signs</title>\n" +
                "\t\t\t\t\t\t\t<text>\n" +
                "\t\t\t\t\t\t\t\t<table>\n" +
                "\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<th>Date / Time</th>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<th>20250407</th>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<th>Altura</th>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td>177 cm</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<th>Peso</th>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td>88.0 kg</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t</text>\n" +
                "\t\t\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t\t\t<code code=\"50373000\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Medicion de altura\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<effectiveTime value=\"200004071430\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<value xsi:type=\"PQ\" value=\"1.77\" unit=\"m\"></value>\n" +
                "\t\t\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t\t\t<entry>\n" +
                "\t\t\t\t\t\t\t\t<observation classCode=\"OBS\" moodCode=\"EVN\">\n" +
                "\t\t\t\t\t\t\t\t\t<code code=\"363808001\" codeSystem=\"2.16.840.1.113883.6.96\" codeSystemName=\"SNOMED CT\" displayName=\"Body weight measure\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<statusCode code=\"completed\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<effectiveTime value=\"200004071430\"/>\n" +
                "\t\t\t\t\t\t\t\t\t<value xsi:type=\"PQ\" value=\"88.0\" unit=\"kg\"></value>\n" +
                "\t\t\t\t\t\t\t\t</observation>\n" +
                "\t\t\t\t\t\t\t</entry>\n" +
                "\t\t\t\t\t\t</section>\n" +
                "\t\t\t\t\t</component>\n" +
                "\t\t\t\t</section>\n" +
                "\t\t\t</component>\n" +
                "\t\t</structuredBody>\n" +
                "\t</component>\n" +
                "</ClinicalDocument>\n");

    }

}
