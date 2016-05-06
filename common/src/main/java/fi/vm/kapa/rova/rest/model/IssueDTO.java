package fi.vm.kapa.rova.rest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueDTO {
    private String uri;
    private Map<String, String> labels = new HashMap<>();
    private List<IssueDTO> subissues = new ArrayList<>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<IssueDTO> getSubissues() {
        return subissues;
    }

    public void setSubissues(List<IssueDTO> subissues) {
        this.subissues = subissues;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "IssueDTO [uri=" + uri + ", labels=" + labels + ", subissues="
                + subissues + "]";
    }

}
