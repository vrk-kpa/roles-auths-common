package fi.vm.kapa.rova.external.model.merlin;

import java.util.List;

public class GuardianTasks {
    private String clientId;
    private List<String> tasks;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
}
