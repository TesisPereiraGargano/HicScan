package uy.com.fing.hicscan.hceanalysis.data.ontologyRepository;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;

@Component
@Slf4j
public class FusekiTripleStoreClient {

    @Value("${fuseki.server.url:http://179.27.97.6:3030}")
    private String fusekiServerUrl;

    @Value("${fuseki.dataset.name:ontology}")
    private String datasetName;

    /**
     * Obtiene una ontología por nombre desde el servidor Fuseki
     *
     * @param ontologyName nombre de la ontología
     * @return modelo RDF de la ontología
     */
    public Model getOntologyByName(String ontologyName) {
        try {
            String sparqlEndpoint = String.format("%s/%s/sparql", fusekiServerUrl, datasetName);
            String query = String.format(
                "CONSTRUCT { ?s ?p ?o } WHERE { GRAPH <%s> { ?s ?p ?o } }",
                ontologyName
            );
            
            log.info("Obteniendo ontología {} desde {}", ontologyName, sparqlEndpoint);
            
            // Crear un modelo vacío
            Model model = ModelFactory.createDefaultModel();
            
            // Ejecutar consulta CONSTRUCT
            String queryUrl = sparqlEndpoint + "?query=" + java.net.URLEncoder.encode(query, "UTF-8");
            InputStream inputStream = new URL(queryUrl).openStream();
            
            // Cargar los datos en el modelo
            RDFDataMgr.read(model, inputStream, null);
            
            log.info("Ontología {} cargada exitosamente. Tamaño: {} triples", 
                    ontologyName, model.size());
            
            return model;
            
        } catch (Exception e) {
            log.error("Error al obtener la ontología {}: {}", ontologyName, e.getMessage(), e);
            throw new RuntimeException("Error al obtener la ontología: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si una ontología existe en el servidor Fuseki
     *
     * @param ontologyName nombre de la ontología
     * @return true si existe, false en caso contrario
     */
    public boolean ontologyExists(String ontologyName) {
        try {
            Model model = getOntologyByName(ontologyName);
            return !model.isEmpty();
        } catch (Exception e) {
            log.warn("La ontología {} no existe o no se pudo acceder: {}", ontologyName, e.getMessage());
            return false;
        }
    }
}
