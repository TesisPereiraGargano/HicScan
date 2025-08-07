package uy.com.fing.hicscan.hceanalysis.languageexpansion;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MedicationDictionary {
    //En el properties puedo definir si quiero utilizar otra versión del diccionario de medicmanetos de AGESIC
    @Value("${DiccionarioMedicamentos.file.path:}") // Si no está, queda vacío
    private String diccionarioMedicamentosFilePath;

    @Getter
    //bidireccional map con <AMP_id, nombre comercial>
    //private List<AbstractMap.SimpleEntry<String, String>> listaNombresMedicamentos = new ArrayList<>();
    private BiMap<String, String> listaNombresMedicamentos = HashBiMap.create();

    @Getter
    //lista con <AMP_id, VMP_id>
    private List<AbstractMap.SimpleEntry<String, String>> listaVMPid = new ArrayList<>();

    @Getter
    //lista con <VMP_id, nombre del principio activo>
    private List<AbstractMap.SimpleEntry<String, String>> listaPrincipiosActivos = new ArrayList<>();

    @PostConstruct
    public void initMedicationDictionary() throws FileNotFoundException {
        //Proceso el archivo xml una sóla vez -- Singleton
        BufferedReader reader;
        File diccionario;
        if (diccionarioMedicamentosFilePath != null && !diccionarioMedicamentosFilePath.isBlank()) {
            // Ruta definida por el usuario
            diccionario = new File(diccionarioMedicamentosFilePath);
            if (!diccionario.exists() || !diccionario.isFile()) {
                throw new FileNotFoundException("Diccionario de medicamentos no se encontro en la ruta: " + diccionarioMedicamentosFilePath);
            }
        } else {
            // Si no se definió ningún archivo de medicamentos en el properties
            diccionario = new File("src/main/java/uy/com/fing/hicscan/hceanalysis/languageexpansion/resources/DiccionarioMedicamentos_38b0e1.xml");
            if (!diccionario.exists()) {
                throw new FileNotFoundException("No se encontro el diccionario de medicamentos por defecto del proyecto");
            }
        }
        log.info("Se cargo el archivo que estaba en la ruta: {}", diccionario.getAbsolutePath());

        //Proceso el archivo para cargar las palabras en un arreglo
        // el archivo se espera que esté en formato xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true); //para manejar los namespaces
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        //Retorno error si no puede generar el parseo
        try {
            doc = dBuilder.parse(diccionario);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        doc.getDocumentElement().normalize();
        //Defino el namespace
        String namespace = "DNMA";

        //Como sólo hay un tag <AMPS> en el archivo quedan todos los AMPS dentro del primer item de amps
        NodeList amps = doc.getElementsByTagNameNS(namespace, "AMPS");
        Element ampsXML = (Element) amps.item(0);
        //Genero una lista con cada AMP
        NodeList amp = ampsXML.getElementsByTagNameNS(namespace, "AMP");

        for (int i = 0; i < amp.getLength(); i++) {
            Element amp_i = (Element) amp.item(i);
            //Obtengo el AMP_ID
            Node amp_id = amp_i.getElementsByTagNameNS(namespace, "AMP_Id").item(0);
            //Obtengo el VMP_ID que es el principio activo relacionado
            Node vmp_id = amp_i.getElementsByTagNameNS(namespace, "VMP_Id").item(0);
            //Obtengo el elemento con la etiqueta <PROD_MSP>
            Node textoProdMsp = amp_i.getElementsByTagNameNS(namespace, "PROD_MSP").item(0);
            //Obtengo el elemento con la etiqueta <AMP_DSC>
            Node textoAmpDsc = amp_i.getElementsByTagNameNS(namespace, "AMP_DSC").item(0);

            String medicamento = "";
            //Obtengo el texto en la etiqueta PROD_MSP si es que lo hay
            if (textoProdMsp != null && textoProdMsp.getTextContent() != null && !textoProdMsp.getTextContent().isBlank()) {
                medicamento = textoProdMsp.getTextContent();
            } else {
                //Si está vacía, le cargo el AMP_DSC
                medicamento = textoAmpDsc.getTextContent();
            }
            //Por lo analizado no podría suceder que ambos estén vacíos, pero agregamos el chequeo
            if (!medicamento.isBlank()) {
                //Elimino texto entre paréntesis
                medicamento = medicamento.replaceAll("\\([^)]*\\)", "");
                //Me quedo con las primeras seis palabras
                medicamento = Arrays.stream(medicamento.split("\\s+"))
                        .limit(6)
                        .collect(Collectors.joining(" "));
                //Si ya no está, agrego el nombre comercial a la lista
                if (!listaNombresMedicamentos.containsValue(medicamento)) {
                    listaNombresMedicamentos.put(amp_id.getTextContent(), medicamento);
                }

                //Agrego la relación AMP_id con VMP_id
                listaVMPid.add(new AbstractMap.SimpleEntry<>(amp_id.getTextContent(), vmp_id.getTextContent()));
            }

        }
        log.info("El listado de medicamentos se cargo y tiene tamaño: {}", listaNombresMedicamentos.size());
        log.info("El listado de principios activos se cargo y tiene tamaño: {}", listaVMPid.size());
    }

}