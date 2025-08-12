package uy.com.fing.hicscan.hceanalysis.data.ctakes;

import jakarta.annotation.PostConstruct;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.core.pipeline.PipelineBuilder;
import org.apache.ctakes.core.pipeline.PiperFileReader;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.MedicationMention;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// para manipular archivos
import java.io.File;
//para procesar el html generado por ctakes
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//importo la clase ApiResponse para el formateo de la respuesta en json
import uy.com.fing.hicscan.hceanalysis.data.ctakes.dto.ApiResponse;

@RestController
@RequestMapping("/ctakes")
public class ctakesController {
    private final AnalysisEngine engine;

    public ctakesController() throws IOException, UIMAException {
        System.setProperty("umlsKey", "9acb4127-e18e-4a0c-a53d-6555dd08fb32");
        PiperFileReader reader = new PiperFileReader("/root/HicScan/src/main/java/uy/com/fing/hicscan/hceanalysis/data/ctakes/BigPipeline.piper");
        PipelineBuilder builder = reader.getBuilder();
        AnalysisEngineDescription pipeline = builder.getAnalysisEngineDesc();
        engine = UIMAFramework.produceAnalysisEngine(pipeline);

    }

    @PostMapping("/getDrugsFromTextV2")
    //Operacion que permite analizar texto plano utilizando la aplicacion Apache ctakes
    //retornando como respuesta el conjunto de medicamentos extraidos de la misma
    public ResponseEntity<ApiResponse> ejecutarPipeline(
            @RequestParam String inputText
    ) throws ResourceInitializationException, AnalysisEngineProcessException {
        JCas jCas = engine.newJCas();
        jCas.setDocumentText(inputText);
        engine.process(jCas);

        Map<String, String> drogas = new HashMap<>(); //hashmap que guarda los medicamentos y su cui
        for (MedicationMention med : JCasUtil.select(jCas, MedicationMention.class)) {
            if (med != null) {
                String nombre = med.getCoveredText();
                String cui = "N/A";

                if (med.getOntologyConceptArr() != null && !med.getOntologyConceptArr().isEmpty()) {
                    cui = med.getOntologyConceptArr(0).getCode();
                }
                drogas.putIfAbsent(cui, nombre); // no sobreescribo si ya estaba el CUI
                System.out.printf("Entidad: %-20s  CUI: %s%n", nombre, cui);
            }
        }

        //retorno la respuesta
        ApiResponse respuesta = new ApiResponse("success", "Datos extraídos correctamente.", drogas);
        return ResponseEntity.ok(respuesta);
        }


    @PostMapping("/getDrugsFromText")
//Operacion que permite analizar texto plano utilizando la aplicacion Apache ctakes
//retornando como respuesta el conjunto de medicamentos extraidos de la misma
    public ResponseEntity<ApiResponse> ejecutarPipeline(
            @RequestParam String pipelinePath,
            @RequestParam String inputText,
            @RequestParam String ctakesHome,
            @RequestParam String umlskey
    ) {
        try {
            //creo el archivo y lo cargo con el texto recibido por parámetro
            File inputFile = new File("entrada.txt");
            if (inputFile.createNewFile()) {
                System.out.println("File created: " + inputFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            Files.write(inputFile.toPath(), inputText.getBytes());
            // Para ejecutarlo en windows
            //genero el comando para poder ejecutar el .bat (que ejecuta a ctakes con esos inputs)
            //List<String> comandoEjecucionCtakes = new ArrayList<>();
            //comandoEjecucionCtakes.add("cmd.exe"); //ejecuta el interprete de comandos de Windows
            //comandoEjecucionCtakes.add("/c"); //indica a cmd que finalice despues de ejecutar el comando
            //comandoEjecucionCtakes.add("D:\\prueba.bat"); // O la ruta absoluta si no está en el mismo directorio

            List<String> comandoEjecucionCtakes = new ArrayList<>();
            comandoEjecucionCtakes.add("/bin/bash"); // ejecuta bash en Linux
            comandoEjecucionCtakes.add("/root/HicScan/src/main/java/uy/com/fing/hicscan/hceanalysis/data/ctakes/ejecutarCtakes.sh");

            comandoEjecucionCtakes.add(pipelinePath);
            comandoEjecucionCtakes.add(inputFile.getAbsolutePath());
            comandoEjecucionCtakes.add(ctakesHome);
            comandoEjecucionCtakes.add(umlskey);

            //Creo archivo temporal donde almacenar la respuesta de ctakes
            Path tempDir = Files.createTempDirectory("ctakes_output");
            String outputPath = tempDir.toAbsolutePath().toString();
            //agrego el argumento a la ejecucion del sh
            comandoEjecucionCtakes.add(outputPath);

            ProcessBuilder builder = new ProcessBuilder(comandoEjecucionCtakes);
            System.out.println("Ejecutando: " + builder.command()); //para ver que va a ejecutar
            builder.redirectErrorStream(true); // combina stdout y stderr

            //ejecuto el proceso
            Process proceso = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proceso.getInputStream())
            );

            StringBuilder salida = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                salida.append(linea).append("\n");
            }

            //logueo la ejecucion del script con errores incluidos en caso de que surjan
            System.out.println(salida.toString());

            //busco en la respuesta de ctakes los medicamentos

            Map<String, String> drogas = new HashMap<>(); //hashmap que guarda los medicamentos y su cui

            //el nombre del archivo que se genera depende directamente del nombre del archivo que recibe el sh como entrada
            Path salidaPath = Paths.get(outputPath, "html_table/entrada_table.HTML");
            File archivo = salidaPath.toFile();
            Document doc = Jsoup.parse(archivo, "UTF-8");

            //obtengo las columnas
            Map<String, Integer> columnas = new HashMap<>();
            Element header = doc.selectFirst("table thead tr");
            if (header != null) {
                Elements ths = header.select("th");
                for (int i = 0; i < ths.size(); i++) {
                    String nombre = ths.get(i).text().trim();
                    columnas.put(nombre, i);
                }
            }

            //me quedo con las columnas que me importan
            int nroColumnaGrupo = columnas.getOrDefault("Semantic Group", -1);
            int nroColumnaCui = columnas.getOrDefault("CUI", -1);
            int nroColumnaNombre = columnas.getOrDefault("Preferred Text", -1);

            //genero una excepción si me falta alguna de las columnas
            if (nroColumnaGrupo == -1 || nroColumnaCui == -1 || nroColumnaNombre == -1) {
                throw new IllegalStateException("Faltan columnas necesarias en el archivo HTML.");
            }

            //obtengo el máximo de los índices para recorrer las filas
            int maximo = Math.max(nroColumnaGrupo, Math.max(nroColumnaCui, nroColumnaNombre));

            // proceso las filas
            Elements filas = doc.select("table tr");
            for (Element fila : filas) {
                Elements tds = fila.select("td");
                if (tds.size() > maximo) {
                    String grupo = tds.get(nroColumnaGrupo).text().trim();
                    if ("Drug".equalsIgnoreCase(grupo)) {
                        String cui = tds.get(nroColumnaCui).text().trim();
                        String nombre = tds.get(nroColumnaNombre).text().trim();
                        drogas.putIfAbsent(cui, nombre); // no sobreescribo si ya estaba el CUI
                    }
                }
            }

            //retorno la respuesta
            ApiResponse respuesta = new ApiResponse("success", "Datos extraídos correctamente.", drogas);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse respuesta = new ApiResponse("error", "Ocurrió un error inesperado: " + e.getMessage(), new HashMap<>());
            return ResponseEntity.status(500).body(respuesta);
        }
    }
}
