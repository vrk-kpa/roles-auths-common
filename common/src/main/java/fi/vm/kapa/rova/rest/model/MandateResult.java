package fi.vm.kapa.rova.rest.model;

public class MandateResult {
    boolean created;
    String uuid;

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
