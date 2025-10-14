package uy.com.fing.hicscan.hceanalysis.data.breastcancer.persistence;

import org.apache.jena.ontology.Individual;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * El objetivo de esta clase es la de almacenar en memoria las instancias de mujer que vamos generando
 * para que luego se puedan acceder en las operaciones de recomendaciòn. No las guardamos en Triplestore
 * porque queda fuera del alcance del proyecto.
 */
@Service
public class WomanIndividualsRepository {

    private final Map<String, Individual> womanIndividuals = new HashMap<>();

    public String saveWomanIndividual(Individual woman) {
        String womanId = UUID.randomUUID().toString(); //genero identificador único random.
        womanIndividuals.put(womanId, woman);
        return womanId;
    }

    public Individual getWoman(String womanId) {
        return womanIndividuals.get(womanId);
    }
}

