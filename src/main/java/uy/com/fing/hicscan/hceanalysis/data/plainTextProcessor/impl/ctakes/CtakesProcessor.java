package uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.impl.ctakes;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uy.com.fing.hicscan.hceanalysis.data.plainTextProcessor.PlainTextProcessor;
import uy.com.fing.hicscan.hceanalysis.dto.CodDiccionario;
import uy.com.fing.hicscan.hceanalysis.dto.Droga;
import uy.com.fing.hicscan.hceanalysis.dto.SustanciaAdministrada;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
public class CtakesProcessor implements PlainTextProcessor {

    @Value("${ctakes.piper.file.path}")
    private String piperFilePath;

    @Value("${umls.key}")
    private String umlsKey;

    private AnalysisEngine engine;

    @PostConstruct
    public void init() throws IOException, UIMAException {
        if (piperFilePath == null || piperFilePath.isBlank()) {
            //Genero un archivo temporal con el contenido del BigPiper
            try (InputStream in = getClass().getResourceAsStream("/BigPipeline.piper")) {
                if (in == null) {
                    throw new FileNotFoundException("No se encontró el archivo interno: BigPipeline.piper, ni se encuentra definida la ruta como propiedad");
                }
                //Copio la ruta física y se la asigno al path
                Path tempFile = Files.createTempFile("pipeline", ".piper");
                Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                piperFilePath = tempFile.toAbsolutePath().toString();
            }
        }
        log.info("El piper ejecutado esta en la ruta: {}", piperFilePath);
        //UMLS key si la necesitas
        System.setProperty("umlsKey", umlsKey);

        //Inicializar cTAKES
        PiperFileReader reader = new PiperFileReader(piperFilePath);
        PipelineBuilder builder = reader.getBuilder();
        AnalysisEngineDescription pipeline = builder.getAnalysisEngineDesc();
        engine = UIMAFramework.produceAnalysisEngine(pipeline);

        log.info("cTAKES inicializado correctamente");
    }


    @Override
    public List<SustanciaAdministrada> extractDrugs(String inputText) {
        JCas jCas = null;
        try {
            jCas = engine.newJCas();
            jCas.setDocumentText(inputText);
            try {
                engine.process(jCas);
                List<SustanciaAdministrada> drogas = new ArrayList<>();
                String[] tuisDrugs = {"T116", "T195", "T123", "T122", "T103", "T120", "T104", "T200",
                        "T196", "T126", "T131", "T125", "T129", "T130", "T197", "T114",
                        "T109", "T121", "T192", "T127"};
                Set<String> cuiSet = new HashSet<>(); //Para no repetir
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
                                    if (!cuiSet.contains(cui)) {
                                        cuiSet.add(cui);
                                        log.info("[CtakesProcessor] extractDrugs - Se extranjo el cui {}", cui);

                                        // Busco RXNORM en otras OntologyConcepts asociadas (puedes tener más de uno)
                                        // Esto porque CTAKES los medicamentos los identifica usando RXNORM
                                        // TO DO: Revisar, no sé si de acá no podría sacar también el de SNOMED CT
                                        // Ni si está bien quedarme sólo con el primero
                                        String cod_rxnorm = "";
                                        for (int j = 0; j < ia.getOntologyConceptArr().size(); j++) {
                                            OntologyConcept oc2 = ia.getOntologyConceptArr(j);
                                            if("RXNORM".equalsIgnoreCase(oc2.getCodingScheme())) {
                                                cod_rxnorm = oc2.getCode();
                                                break;
                                            }
                                        }

                                        CodDiccionario cod_drugs = new CodDiccionario();
                                        cod_drugs.setCui(umls.getCui());
                                        cod_drugs.setRxnorm(cod_rxnorm);

                                        Droga droga = new Droga(cod_drugs,"" ); //No hay un GetName en el Ontology Concept
                                        List<Droga> drugs = new ArrayList<>();
                                        drugs.add(droga);
                                        SustanciaAdministrada sust = new SustanciaAdministrada(umls.getPreferredText(),"","","","",drugs);
                                        drogas.add(sust); //código en RXNORM y nombre del medicamento
                                    }

                                }
                            }
                        }
                    }
                }
                log.info("Los medicamentos extraidos son: {}", drogas);
                return drogas;
            } catch (AnalysisEngineProcessException e) {
                throw new RuntimeException("Error al crear nueva engine Ctakes", e);
            }
        } catch (ResourceInitializationException e) {
            throw new RuntimeException("Error al procesar usando el jcas Ctakes (engine.process)",e);
        }
    }
}
