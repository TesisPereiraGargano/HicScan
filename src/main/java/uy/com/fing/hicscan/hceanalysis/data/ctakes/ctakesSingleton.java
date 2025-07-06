package uy.com.fing.hicscan.hceanalysis.data.ctakes;

import org.apache.ctakes.pipeline.PiperFileReader;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.factory.JCasFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ctakesSingleton {

    private AnalysisEngine analysisEngine;

    // Solo se carga una vez al crear el servicio de springboot
    public ctakesSingleton() throws Exception {
        String piperPath = "root/apache-ctakes-6.0.0/resources/org/apache/ctakes/examples/pipeline/BigPipeline.piper";  // TO DO: cambiarlo para que se cargue por properties
        PiperFileReader reader = new PiperFileReader();
        reader.loadPipelineDescription(piperPath);
        this.analysisEngine = reader.getAnalysisEngine();
    }

    public String processTextCtakes(String textoPlano) throws Exception {
        // Crear carpeta temporal de salida
        Path outputPath = Files.createTempDirectory("ctakes_output");
        System.setProperty("ctakes.output.dir", outputPath.toAbsolutePath().toString());

        // Creo el JCas y le paso el texto a procesar
        JCas jcas = JCasFactory.createJCas();
        jcas.setDocumentText(textoPlano);

        // Procesar con el pipeline cargado
        analysisEngine.process(jcas);

        // Devolver la ruta donde están los archivos generados (para que el controller lo use)
        return outputPath.toString();
    }
}
