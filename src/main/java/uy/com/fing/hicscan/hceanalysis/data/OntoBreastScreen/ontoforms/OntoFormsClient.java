package uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontoforms;

import lombok.extern.slf4j.Slf4j;
import uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.ontology.BCRClassesEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import static uy.com.fing.hicscan.hceanalysis.data.OntoBreastScreen.persistence.BreastCancerFusekiClient.NAMED_GRAPH_PREFIX;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class OntoFormsClient {

    @Value("${ontoforms.client.url}")
    private String HOST;

    private final RestClient restClient;

    public OntoFormsClient() {
        restClient = RestClient.create();
    }


    public String getOntologyFileName() {
        URI uri = UriComponentsBuilder
                .fromUriString(HOST + "apps/hicscan/configurations/app-mapping")
                .build("OntoBreastScreen");


        List<String> ontoFullNames = restClient.get().uri(uri)
                .retrieve()
                .body(ParameterizedTypeReference.forType(ResolvableType.forClassWithGenerics(List.class,
                        String.class).getType()));

        String res = ontoFullNames.get(0).replace(NAMED_GRAPH_PREFIX, "");
        return res;
    }

    public Form getWomanForm(){
        String ontoId = getOntologyFileName();

        URI uri = UriComponentsBuilder
                .fromUriString(HOST + "ontologies/{ontoId}/forms")
                .queryParam("classUri", BCRClassesEnum.WOMAN_CLASS.getUri())
                .build(ontoId);

        return restClient.get().uri(uri)
                .retrieve()
                .body(Form.class);
    }

}

