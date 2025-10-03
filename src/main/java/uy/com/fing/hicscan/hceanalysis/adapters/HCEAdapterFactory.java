package uy.com.fing.hicscan.hceanalysis.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import uy.com.fing.hicscan.hceanalysis.adapters.impl.FHIR_Adapter;
import uy.com.fing.hicscan.hceanalysis.adapters.impl.HL7v3_CDAr2_Adapter;

public class HCEAdapterFactory {

    public static HCEAdapter getAdapter(File file) throws IOException {
        String extension = getFileExtension(file);

        return switch (extension) {
            case "xsd" -> getXmlAdapter(file); // analizo los distintos tipos que pueden ser FHIR, CCDA, CDA, etc
            //case "csv" -> new CSV_Adapter(); si se quisieran agregar mas formatos
            //case "json" -> new Json_Adapter();
            default -> throw new IllegalArgumentException("Extension no soportada: " + extension);
        };
    }

    private static HCEAdapter getXmlAdapter(File file) throws IOException {
        String rootElement = getRootElementName(file);

        return switch (rootElement) {
            case "ClinicalDocument" -> new HL7v3_CDAr2_Adapter();
            case "Bundle", "Patient", "Observation" -> new FHIR_Adapter(); //Posible ejemplo de expansion a FHIR
            default -> throw new IllegalArgumentException("Formato XML no soportado: " + rootElement);
        };
    }

    private static String getRootElementName(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            return doc.getDocumentElement().getLocalName();
        } catch (Exception e) {
            throw new IOException("Error leyendo el XML", e);
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex == -1) return "";
        return name.substring(lastIndex + 1).toLowerCase();
    }
}

