package fi.vm.kapa.rova.ontology;

import fi.vm.kapa.rova.rest.AbstractClient;

import javax.ws.rs.core.GenericType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.text.MessageFormat.format;

/**
 * Created by Juha Korkalainen on 25.8.2016.
 */
public class OntologyClient extends AbstractClient {

    public OntologyClient(String endPointUrl) {
        super(endPointUrl);
    }

    public Concept getConcept(String uri) {
        return getGeneric(format("/rest/ontology/concept/{0}", uri), new GenericType<Concept>() {});
    }

    public List<Concept> getConcepts() {
        return getGeneric(format("/rest/ontology/concept/all"), new GenericType<List<Concept>>() {});
    }

    public Boolean isBroaderConcept(String broaderUri, String narrowerUri) {
        Map<String, Object> params = new HashMap<>();
        params.put("broader-uri", broaderUri);
        params.put("narrower-uri", narrowerUri);
        return getGeneric(format("/rest/ontology/concept/is-broader-concept", params), new GenericType<Boolean>() {});
    }

    public Set<Concept> getNarrowerConcepts(String uri) {
        Map<String, Object> params = new HashMap<>();
        params.put("broader-uri", uri);
        return getGeneric(format("/rest/ontology/concept/narrower", params), new GenericType<Set<Concept>>() {});
    }

}
