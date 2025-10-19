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
/**
 * Clase que permite la manipulación y validación de documentos XML.
 *
 * Incluye métodos para validar documentos XML contra esquemas XSD,
 * extraer información mediante expresiones XPath y obtener valores
 * de elementos y atributos de forma simplificada.
 *
 * Estos métodos son útiles para el procesamiento de documentos clínicos
 * en formato CDA (Clinical Document Architecture) dentro del ecosistema de HicScan.
 */
public class XmlUtils {

    /**
     * Valida un documento XML contra un esquema XSD indicado.
     * @param xml ruta del archivo XML a validar.
     * @param xsd ruta del archivo XSD usado para la validación.
     * @return {@code true} si el XML es válido, {@code false} si ocurre una excepción.
     */
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

    /**
     * Extrae elementos del documento XML que coincidan con una expresión XPath.
     * @param xmlFilePath ruta del archivo XML.
     * @param xpathInput expresión XPath a evaluar.
     * @return lista de elementos {@link Element} resultantes que coinciden con el XPath.
     * @throws Exception si ocurre un error de lectura, análisis o evaluación XPath.
     */    public static List<Element> extraerInfoByXPath(String xmlFilePath, String xpathInput) throws Exception {
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

    /**
     * Extrae valores de texto o atributos desde una lista de elementos XML.
     * Ejemplo:
     * <pre>
     * &lt;code code="XYZ123" displayName="Paracetamol 500mg"&gt; texto123 &lt;/code&gt;
     * </pre>
     *
     * Llamado con `atributo = "displayName"` retorna "Paracetamol 500mg".
     * Llamado con `atributo = null` retorna "texto123".
     *
     * @param elementos lista de elementos base sobre los cuales buscar.
     * @param subElemento nombre del subelemento a extraer.
     * @param atributo nombre del atributo a obtener; puede ser nulo para obtener el texto interior.
     * @return lista de valores obtenidos según el atributo o el contenido textual de los elementos.
     */
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


