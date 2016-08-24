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

import java.util.HashSet;
import java.util.Set;

public class Concept {

    private long id;
    private String uri;
    private Set<String> broaderConcepts = new HashSet<>();
    private Set<String> conceptMatches = new HashSet<>();
    private Set<Label> labels = new HashSet<>();
    private Set<Label> definitions = new HashSet<>();

    public Concept() {
        // NOP
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Set<String> getBroaderConcepts() {
        return broaderConcepts;
    }

    public void setBroaderConcepts(Set<String> broaderConcepts) {
        this.broaderConcepts = broaderConcepts;
    }

    public Set<String> getConceptMatches() {
        return conceptMatches;
    }

    public void setConceptMatches(Set<String> conceptMatches) {
        this.conceptMatches = conceptMatches;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
    public Set<Label> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Set<Label> definitions) {
        this.definitions = definitions;
    }

}
