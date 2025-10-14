package uy.com.fing.hicscan.hceanalysis.data.breastcancer.persistence;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BreastCancerFusekiClient {


    @Value("${triplestore.client.url}")
    private String host;

    public static final String NAMED_GRAPH_PREFIX = "http://www.fing.edu.uy/ontologies/";

    /**
     * Retorna un modelo representando a la ontología pasada por parámetro. Se recupera del triple-store.
     * @param ontoId puede ser la uri completa o solamente la terminación
     * @return
     */
    public Model getOntologyByName(String ontoId) {
        String graphName = ontoId.startsWith(NAMED_GRAPH_PREFIX) ? ontoId : NAMED_GRAPH_PREFIX + ontoId;

        try (RDFConnection conn = RDFConnectionFuseki.connect(host + "/ontologies")) {
            return conn.fetch(graphName);
        }
    }


}

