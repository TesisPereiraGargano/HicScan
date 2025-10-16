package uy.com.fing.hicscan.hceanalysis.utils;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.IOException;

import static uy.com.fing.hicscan.hceanalysis.utils.FunctionUtils.limpiarString;

public class XmlUtils {
    //Metodo para validar un XML usando un XSD
    public static boolean validarXML(String xml, String xsd){
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xml)));
            //Si no salta ninguna excepcion entonces es valido
            return true;
        } catch (SAXException | IOException e) {
            //Si no salta ninguna excepcion entonces es valido
            return false;
        }
    }

    //Metodo para extraer información de un XML dado un XPath
    public static List<Element> extraerInfoByXPath(String xmlFilePath, String xpathInput) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlFilePath));
        XPath xpath = XPathFactory.newInstance().newXPath();

        NodeList nodes = (NodeList) xpath.compile(xpathInput).evaluate(doc, XPathConstants.NODESET);

        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }
        return elements;
    }


    //Recorre la lista de elementos y devuelve los valores de atributo correspondientes a los
    //elementos subElemento, si viene en null devuelve el texto dentro del elemento directamente.
    //Ejemplo:
    // <code code="XYZ123" displayName="Paracetamol 500mg"> texto123 </code>
    // Si invoco con el atributo "displayName" me retorna "Paracetamol 500mg"
    // Si no invoco con un atributo me retorna "texto123"
    // Sirve para extracciones sencillas a un primer nivel
    public static List<String> extraerDeElementos(List<Element> elementos, String subElemento, String atributo) {
        List<String> resultados = new ArrayList<>();

        for (Element elem : elementos) {
            NodeList hijos = elem.getElementsByTagName(subElemento);
            if (hijos.getLength() > 0) {
                Element subElem = (Element) hijos.item(0);
                if (atributo == null || atributo.isEmpty()) {
                    resultados.add(subElem.getTextContent().trim());
                } else {
                    String valAtrib = subElem.getAttribute(atributo);
                    if (!valAtrib.isEmpty()) {
                        resultados.add(valAtrib);
                    }
                }
            }
        }
        return resultados;
    }

    /**
     * Extrae el valor de un subelemento dentro de un elemento XML dado.
     *
     * @param elemento El nodo XML padre donde buscar el subelemento.
     * @param nombreElemento El nombre del subelemento cuyo valor se extraerá.
     * @param atributo (Opcional) El nombre del atributo que se desea extraer del subelemento.
     *                  Si es null o vacío, se devuelve el texto interno del subelemento.
     * @return El valor del atributo o el texto del subelemento. Devuelve null si el subelemento o atributo no existen o están vacíos.
     *
     * Es como la función anterior pero para un elemento dado
     */
    public static String extraerValorDeElemento(Element elemento, String nombreElemento, String atributo) {
        NodeList hijos = elemento.getElementsByTagName(nombreElemento);
        if (hijos.getLength() == 0) return null;

        Element hijo = (Element) hijos.item(0);

        if (atributo == null || atributo.isEmpty()) {
            return limpiarString(hijo.getTextContent());
        } else {
            String val = hijo.getAttribute(atributo);
            return val.isEmpty() ? null : limpiarString(val);
        }
    }

}


