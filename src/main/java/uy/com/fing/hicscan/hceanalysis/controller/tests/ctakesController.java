package uy.com.fing.hicscan.hceanalysis.controller.tests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.apache.ctakes.core.pipeline.PipelineBuilder;
import org.apache.ctakes.core.pipeline.PiperFileReader;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.impl.ctakes.dto.ApiResponse;
@Slf4j
@RestController
@RequestMapping("/ctakes")
public class ctakesController {
    @Value("${ctakes.dictionary.path}")
    private String dictionaryPath;

    @Value("${ctakes.piper.file.path}")
    private String piperFilePath;

    @Value("${umls.key}")
    private String umlsKey;

    private final AnalysisEngine engine;

    private static final String rutaEnProyecto = "/org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab/";



    public ctakesController() throws IOException, UIMAException {
        Path targetPath = Paths.get(rutaEnProyecto);
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        String[] files = { "sno_rx_16ab.script", "sno_rx_16ab.properties" };
        ClassLoader classLoader = getClass().getClassLoader();

        for (String file : files) {
            InputStream is = classLoader.getResourceAsStream(dictionaryPath + file);
            if (is == null) {
                throw new RuntimeException("No se encontró el recurso: " + dictionaryPath + file);
            }
            Files.copy(is, targetPath.resolve(file), StandardCopyOption.REPLACE_EXISTING);
            log.info("Se copio correctamente el archivo {} a la ruta {}",file, dictionaryPath);
        }

        System.setProperty("umlsKey", umlsKey);
        PiperFileReader reader = new PiperFileReader(piperFilePath);
        PipelineBuilder builder = reader.getBuilder();
        AnalysisEngineDescription pipeline = builder.getAnalysisEngineDesc();
        engine = UIMAFramework.produceAnalysisEngine(pipeline);

    }

    @PostMapping("/getDrugsFromText")
    //Operacion que permite analizar texto plano utilizando la aplicacion Apache ctakes
    //retornando como respuesta el conjunto de medicamentos extraidos de la misma
    public ResponseEntity<ApiResponse> ejecutarPipeline(
            @RequestParam String inputText
    ) throws ResourceInitializationException, AnalysisEngineProcessException {
        JCas jCas = engine.newJCas();
        jCas.setDocumentText(inputText);
        engine.process(jCas);

        Map<String, String> drogas = new HashMap<>(); //hashmap que guarda los medicamentos y su cui

        // Lista de TUIs que te interesan
        String[] tuisDrugs = {
                "T116", "T195", "T123", "T122", "T103", "T120", "T104", "T200",
                "T196", "T126", "T131", "T125", "T129", "T130", "T197", "T114",
                "T109", "T121", "T192", "T127"
        };


        for (IdentifiedAnnotation ia : JCasUtil.select(jCas, IdentifiedAnnotation.class)) {
            if (ia.getOntologyConceptArr() != null) {
                for (int i = 0; i < ia.getOntologyConceptArr().size(); i++) {
                    OntologyConcept oc = ia.getOntologyConceptArr(i);
                    if (oc instanceof UmlsConcept umls) {
                        String tui = umls.getTui();
                        boolean esMedicamento = false;
                        int k = 0;
                        while (k < tuisDrugs.length && !esMedicamento) {
                            esMedicamento = tui.startsWith(tuisDrugs[k]);
                            k++;
                        }
                        if (tui != null && esMedicamento) { // T121, T122, etc.
                            String nombre = ia.getCoveredText();
                            String cui = umls.getCui();
                            System.out.printf("Medicamento: %-20s  CUI: %s  TUI: %s%n",
                                    nombre, cui, tui);
                            drogas.putIfAbsent(cui, nombre); // no sobreescribo si ya estaba el CUI;
                        }
                    }
                }
            }
        }


        //retorno la respuesta
        ApiResponse respuesta = new ApiResponse("success", "Datos extraídos correctamente.", drogas);
        return ResponseEntity.ok(respuesta);
    }
}
