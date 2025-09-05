package uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.impl.ctakes;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ctakes.core.pipeline.PipelineBuilder;
import org.apache.ctakes.core.pipeline.PiperFileReader;
import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.PlainTextProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CtakesProcessor implements PlainTextProcessor {

    @Value("${ctakes.dictionary.path}")
    private String dictionaryPath;

    @Value("${ctakes.piper.file.path}")
    private String piperFilePath;

    @Value("${umls.key}")
    private String umlsKey;

    private AnalysisEngine engine;
    
    private static final String rutaEnProyecto = "/org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab/";

    @PostConstruct
    public void init() throws IOException, UIMAException {
        Path targetPath = Paths.get(rutaEnProyecto);
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        String[] files = {"sno_rx_16ab.script", "sno_rx_16ab.properties"};
        ClassLoader classLoader = getClass().getClassLoader();

        for (String file : files) {
            InputStream is = classLoader.getResourceAsStream(dictionaryPath + file);
            if (is == null) {
                throw new RuntimeException("No se encontró el recurso: " + dictionaryPath + file);
            }
            Files.copy(is, targetPath.resolve(file), StandardCopyOption.REPLACE_EXISTING);
            log.info("Se copió correctamente el archivo {} a la ruta {}", file, dictionaryPath);
        }

        System.setProperty("umlsKey", umlsKey);
        PiperFileReader reader = new PiperFileReader(piperFilePath);
        PipelineBuilder builder = reader.getBuilder();
        AnalysisEngineDescription pipeline = builder.getAnalysisEngineDesc();
        engine = org.apache.uima.UIMAFramework.produceAnalysisEngine(pipeline);
    }

    @Override
    public Map<String, String> extractDrugs(String inputText) {
        JCas jCas = null;
        try {
            jCas = engine.newJCas();
            jCas.setDocumentText(inputText);
            try {
                engine.process(jCas);
                Map<String, String> drogas = new HashMap<>();
                String[] tuisDrugs = {"T116", "T195", "T123", "T122", "T103", "T120", "T104", "T200",
                        "T196", "T126", "T131", "T125", "T129", "T130", "T197", "T114",
                        "T109", "T121", "T192", "T127"};

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
                                if (tui != null && esMedicamento) {
                                    String nombre = ia.getCoveredText();
                                    String cui = umls.getCui();
                                    drogas.putIfAbsent(cui, nombre);
                                }
                            }
                        }
                    }
                }
                return drogas;
            } catch (AnalysisEngineProcessException e) {
                throw new RuntimeException("Error al crear nueva engine Ctakes", e);
            }
        } catch (ResourceInitializationException e) {
            throw new RuntimeException("Error al procesar usando el jcas Ctakes (engine.process)",e);
        }
    }
}
