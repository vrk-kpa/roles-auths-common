/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.ontology;

import fi.vm.kapa.rova.rest.AbstractClient;

import javax.ws.rs.core.GenericType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;

/**
 * Created by Juha Korkalainen on 25.8.2016.
 */
public class OntologyClient extends AbstractClient {

    public OntologyClient(String endPointUrl) {
        super(endPointUrl);
    }

    public Concept getConcept(String uri) {
        return getGeneric("/rest/ontology/concept", singletonMap("uri", uri), new GenericType<Concept>() {});
    }

    public List<Concept> getConcepts() {
        return getGeneric("/rest/ontology/concept/all", new GenericType<List<Concept>>() {});
    }

    public List<Concept> getConcepts(Set<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return emptyList();
        }
        return getGeneric("/rest/ontology/concepts", singletonMap("uris", uris), new GenericType<List<Concept>>() {});
    }

    public Boolean isBroaderConcept(String broaderUri, String narrowerUri) {
        Map<String, Object> params = new HashMap<>();
        params.put("broader-uri", broaderUri);
        params.put("narrower-uri", narrowerUri);
        return getGeneric("/rest/ontology/concept/is-broader-concept", params, new GenericType<Boolean>() {});
    }

    public Set<Concept> getNarrowerConcepts(String uri) {
        Map<String, Object> params = new HashMap<>();
        params.put("uri", uri);
        return getGeneric("/rest/ontology/concept/narrower", params, new GenericType<Set<Concept>>() {});
    }

}
