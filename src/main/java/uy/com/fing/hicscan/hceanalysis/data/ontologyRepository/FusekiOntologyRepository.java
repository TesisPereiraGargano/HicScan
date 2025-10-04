package uy.com.fing.hicscan.hceanalysis.data.ontologyRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class FusekiOntologyRepository {

    private final FusekiTripleStoreClient tripleStore;

    /**
     * Genera un ontology model a partir de un Id de ontología.
     * Esta función se utiliza para recuperar el TBox y por tanto tiene un razonador
     * específico.
     *
     * @param ontoId identificador de la ontología (filename)
     * @return modelo.
     */
    public OntModel getOntologyModelForTBoxById(String ontoId) {
        return getOntologyModelById(ontoId, OntModelSpec.OWL_MEM_TRANS_INF);
    }

    /**
     * Genera un ontology model a partir de un Id de ontología.
     * Esta función se utiliza para recuperar el ABox y por tanto tiene un razonador
     * específico.
     *
     * @param ontoId identificador de la ontología (filename)
     * @return modelo.
     */
    public OntModel getOntologyModelABoxByIdFor(String ontoId){
        return getOntologyModelById(ontoId, OntModelSpec.OWL_MEM_RDFS_INF);
    }

    private OntModel getOntologyModelById(String ontoId, OntModelSpec ontModelSpec) {
        Model model = tripleStore.getOntologyByName(ontoId);
        OntModel ontologyModel = ModelFactory.createOntologyModel(ontModelSpec, model);
        ontologyModel.setDerivationLogging(true);
        return ontologyModel;
    }

}
